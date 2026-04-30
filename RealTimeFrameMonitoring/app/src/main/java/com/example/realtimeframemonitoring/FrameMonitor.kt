package com.example.realtimeframemonitoring

import android.util.Log
import android.view.Choreographer
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent

class FrameMonitor {

    private val choreographer by lazy { Choreographer.getInstance() }
    private var lastFrameTimeNanos = 0L

    private val _fps = mutableIntStateOf(0)
    val fps: IntState = _fps

    private val _jankCount = mutableIntStateOf(0)
    val jankCount: IntState = _jankCount

    private val _smoothFrameCount = mutableIntStateOf(0)
    val smoothFrameCount: IntState = _smoothFrameCount

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastFrameTimeNanos != 0L) {
                val frameDurationNanos = frameTimeNanos - lastFrameTimeNanos
                val frameDurationMs = frameDurationNanos / 1_000_000
                
                if (frameDurationNanos > 0) {
                    _fps.intValue = (1_000_000_000 / frameDurationNanos).toInt()
                }

                if (frameDurationMs > 16) {
                    _jankCount.intValue += 1
                } else {
                    _smoothFrameCount.intValue += 1
                }
            }
            lastFrameTimeNanos = frameTimeNanos
            choreographer.postFrameCallback(this)
        }
    }

    fun start() {
        choreographer.postFrameCallback(frameCallback)
    }

    fun stop() {
        choreographer.removeFrameCallback(frameCallback)
    }

    fun reset() {
        _jankCount.intValue = 0
        _smoothFrameCount.intValue = 0
    }

    fun monitorViewJank(view: View) {
        val startTime = System.nanoTime()
        view.viewTreeObserver.addOnPreDrawListener {
            val durationMs = (System.nanoTime() - startTime) / 1_000_000
            if (durationMs > 16) {
                Log.d("FrameMonitorXML", "${view.id} slow draw: $durationMs ms")
            }
            true
        }
    }

    @Composable
    fun MonitorComposableJank(content: @Composable () -> Unit) {
        Box(modifier = Modifier.drawWithContent {
            val start = System.nanoTime()
            drawContent()
            val durationMs = (System.nanoTime() - start) / 1_000_000
            if (durationMs > 16) {
                Log.d("ComposeJank", "Composable slow draw: $durationMs ms")
            }
        }) {
            content()
        }
    }
}
