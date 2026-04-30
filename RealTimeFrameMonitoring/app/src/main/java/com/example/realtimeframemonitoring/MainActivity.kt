package com.example.realtimeframemonitoring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val frameMonitor = FrameMonitor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ProfilerViewModel = viewModel(
                factory = ProfilerViewModelFactory(frameMonitor)
            )

            // Lifecycle management via DisposableEffect
            DisposableEffect(Unit) {
                viewModel.startMonitoring()
                onDispose {
                    viewModel.stopMonitoring()
                }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfilerScreen(viewModel, frameMonitor)
                }
            }
        }
    }
}

@Composable
fun ProfilerScreen(viewModel: ProfilerViewModel, frameMonitor: FrameMonitor) {
    val loadType by viewModel.loadType
    val showInfoDialog by viewModel.showInfoDialog

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Header(onInfoClick = { viewModel.toggleInfoDialog() })

        Spacer(modifier = Modifier.height(16.dp))

        StatsCard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(20.dp))

        ControlButtons(
            currentLoad = loadType,
            onLoadChange = { viewModel.setLoadType(it) },
            onStop = { viewModel.stopLoad() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        frameMonitor.MonitorComposableJank {
            ContentList(
                loadType = loadType,
                onRecompose = { viewModel.incrementRecompositionCount() }
            )
        }
    }

    if (showInfoDialog) {
        InfoDialog(onDismiss = { viewModel.toggleInfoDialog() })
    }
}
