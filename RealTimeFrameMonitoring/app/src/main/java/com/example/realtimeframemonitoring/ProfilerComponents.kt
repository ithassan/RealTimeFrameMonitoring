package com.example.realtimeframemonitoring

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

// Scientific Workload: Enough to be felt, but not enough to crash the OS
const val WORK_LOAD = 150000 

@Composable
fun Header(onInfoClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Performance Lab", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StatsCard(viewModel: ProfilerViewModel) {
    val fps by viewModel.fps
    val cpuUsage by viewModel.cpuUsage
    val memoryUsage by viewModel.memoryUsage
    val smoothCount by viewModel.smoothFrameCount
    val jankCount by viewModel.jankCount
    val recompositionCount by viewModel.recompositionCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(label = "FPS", value = fps.toString(), color = if (fps < 50) Color.Red else Color(0xFF4CAF50))
                StatItem(label = "CPU", value = String.format(Locale.US, "%.1f%%", cpuUsage), color = Color(0xFF2196F3))
                StatItem(label = "RAM", value = "${memoryUsage}MB", color = Color(0xFF9C27B0))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(label = "Smooth", value = smoothCount.toString(), color = Color(0xFF4CAF50))
                StatItem(label = "Jank", value = jankCount.toString(), color = if (jankCount > 0) Color.Red else Color.Gray)
                StatItem(label = "Recomp", value = recompositionCount.toString(), color = if (recompositionCount > 500) Color.Red else Color(0xFFFF9800))
            }
            Button(
                onClick = { viewModel.resetStats() },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset Counters", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ControlButtons(currentLoad: LoadType, onLoadChange: (LoadType) -> Unit, onStop: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (currentLoad == LoadType.NONE) "Select Optimization Tier:" else "Active Simulation:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AnimatedContent(
            targetState = currentLoad,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
            label = "ControlButtonsTransition"
        ) { targetLoad ->
            if (targetLoad == LoadType.NONE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LoadButton("🔴 BAD", Color(0xFFEF5350)) { onLoadChange(LoadType.BAD) }
                    LoadButton("🟡 BETTER", Color(0xFFFFCA28)) { onLoadChange(LoadType.BETTER) }
                    LoadButton("🟢 BEST", Color(0xFF66BB6A)) { onLoadChange(LoadType.BEST) }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (label, color) = when (targetLoad) {
                        LoadType.BAD -> "🔴 BAD MODE" to Color(0xFFEF5350)
                        LoadType.BETTER -> "🟡 BETTER MODE" to Color(0xFFFFCA28)
                        LoadType.BEST -> "🟢 BEST MODE" to Color(0xFF66BB6A)
                        else -> "" to Color.Gray
                    }

                    Surface(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = if (color == Color(0xFFFFCA28)) Color.Black else color,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text("STOP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.LoadButton(label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ContentList(loadType: LoadType, onRecompose: () -> Unit = {}) {
    AnimatedContent(
        targetState = loadType,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "ContentListTransition",
        modifier = Modifier.fillMaxSize()
    ) { targetLoad ->
        if (targetLoad == LoadType.NONE) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Info, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp), 
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select a mode above to start simulation", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items((1..100).toList(), key = { it }) { i ->
                    SideEffect { onRecompose() }
                    
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { visible = true }
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        when (targetLoad) {
                            LoadType.BAD -> HeavyItem(i)
                            LoadType.BETTER -> BetterItem(i)
                            LoadType.BEST -> BestItem(i)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeavyItem(index: Int) {
    Box(modifier = Modifier.fillMaxWidth().drawWithContent {
        repeat(WORK_LOAD) { Math.sqrt(it.toDouble()) }
        drawContent()
    }) {
        Card(
            modifier = Modifier.fillMaxSize(), 
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "❌ BAD #$index: Math on every Draw frame", modifier = Modifier.padding(16.dp), fontSize = 12.sp)
        }
    }
}

@Composable
fun BetterItem(index: Int) {
    val result = remember {
        repeat(WORK_LOAD) { Math.sqrt(it.toDouble()) }
        "Cached"
    }
    Card(
        modifier = Modifier.fillMaxWidth(), 
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = "⚠️ BETTER #$index: $result (Blocks only once)", modifier = Modifier.padding(16.dp), fontSize = 12.sp)
    }
}

@Composable
fun BestItem(index: Int) {
    var status by remember { mutableStateOf("Calculating...") }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            repeat(WORK_LOAD) { Math.sqrt(it.toDouble()) }
            status = "Done"
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(), 
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "✅ BEST #$index: $status", modifier = Modifier.weight(1f), fontSize = 12.sp)
            if (status == "Calculating...") {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            }
        }
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Performance Classroom", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                LessonSection("📊 RECOMPOSITION COUNT", "Shows how many times the UI items were re-drawn. In Compose, frequent recomposition can lead to lag if not optimized.", Color(0xFFFF9800))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                LessonSection("🔴 BAD MODE", "Math runs in 'drawWithContent'. This forces the CPU to calculate square roots 60 times every second for every visible item.", Color.Red)
                LessonSection("🟡 BETTER MODE", "Math runs in 'remember'. It blocks the UI thread for ~100ms only the first time an item appears. Fast after caching, but causes 'jumps' during scrolling.", Color(0xFFD4AF37))
                LessonSection("🟢 BEST MODE", "Math runs in 'LaunchedEffect' on a background thread. The UI thread never stops. The phone stays responsive while work happens in the background.", Color.Green)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                LessonSection("💡 TIP", "Use 'remember' and 'background threads' to keep your UI smooth. Avoid heavy calculations during the draw or measure phases!", Color.Gray)
            }
        },
        confirmButton = { 
            TextButton(onClick = onDismiss) { 
                Text("Dismiss", fontWeight = FontWeight.Bold) 
            } 
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun LessonSection(title: String, content: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = color)
        Text(content, fontSize = 13.sp, fontFamily = FontFamily.Monospace, lineHeight = 18.sp)
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    val animatedColor by animateColorAsState(targetValue = color, animationSpec = tween(400), label = "StatColorAnimation")
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = animatedColor)
    }
}
