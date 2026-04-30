package com.example.realtimeframemonitoring

import android.os.Process
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.RandomAccessFile

class ProfilerViewModel(private val frameMonitor: FrameMonitor) : ViewModel() {

    private val _loadType = mutableStateOf(LoadType.NONE)
    val loadType: State<LoadType> = _loadType

    private val _showInfoDialog = mutableStateOf(false)
    val showInfoDialog: State<Boolean> = _showInfoDialog

    private val _cpuUsage = mutableDoubleStateOf(0.0)
    val cpuUsage: State<Double> = _cpuUsage

    private val _memoryUsage = mutableLongStateOf(0L)
    val memoryUsage: State<Long> = _memoryUsage

    private val _recompositionCount = mutableIntStateOf(0)
    val recompositionCount: State<Int> = _recompositionCount

    val fps = frameMonitor.fps
    val jankCount = frameMonitor.jankCount
    val smoothFrameCount = frameMonitor.smoothFrameCount

    init {
        startPerformanceTracking()
    }

    private fun startPerformanceTracking() {
        viewModelScope.launch {
            while (true) {
                updateMemoryUsage()
                updateCpuUsage()
                delay(1000)
            }
        }
    }

    private fun updateMemoryUsage() {
        val runtime = Runtime.getRuntime()
        _memoryUsage.longValue = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    }

    private fun updateCpuUsage() {
        try {
            val reader = RandomAccessFile("/proc/self/stat", "r")
            val line = reader.readLine()
            val toks = line.split(" ")
            val utime = toks[13].toLong()
            val stime = toks[14].toLong()
            val totalTime = utime + stime
            _cpuUsage.doubleValue = totalTime.toDouble() % 100 
            reader.close()
        } catch (e: Exception) {
            _cpuUsage.doubleValue = 0.0
        }
    }

    fun incrementRecompositionCount() {
        _recompositionCount.intValue++
    }

    fun setLoadType(type: LoadType) {
        _loadType.value = if (_loadType.value == type) LoadType.NONE else type
    }

    fun stopLoad() {
        _loadType.value = LoadType.NONE
    }

    fun toggleInfoDialog() {
        _showInfoDialog.value = !_showInfoDialog.value
    }

    fun resetStats() {
        frameMonitor.reset()
        _recompositionCount.intValue = 0
    }

    fun startMonitoring() {
        frameMonitor.start()
    }

    fun stopMonitoring() {
        frameMonitor.stop()
    }
}
