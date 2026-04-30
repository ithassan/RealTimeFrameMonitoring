package com.example.realtimeframemonitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfilerViewModelFactory(private val frameMonitor: FrameMonitor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfilerViewModel(frameMonitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
