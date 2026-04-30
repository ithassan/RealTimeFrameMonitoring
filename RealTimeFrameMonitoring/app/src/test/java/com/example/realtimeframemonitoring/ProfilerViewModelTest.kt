package com.example.realtimeframemonitoring

import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilerViewModelTest {

    private lateinit var frameMonitor: FrameMonitor
    private lateinit var viewModel: ProfilerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        frameMonitor = mock(FrameMonitor::class.java)
        
        // Stub the properties that ViewModel accesses during init
        `when`(frameMonitor.fps).thenReturn(mutableIntStateOf(0))
        `when`(frameMonitor.jankCount).thenReturn(mutableIntStateOf(0))
        `when`(frameMonitor.smoothFrameCount).thenReturn(mutableIntStateOf(0))
        
        viewModel = ProfilerViewModel(frameMonitor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load type is NONE`() {
        assertEquals(LoadType.NONE, viewModel.loadType.value)
    }

    @Test
    fun `setLoadType updates load type`() {
        viewModel.setLoadType(LoadType.BAD)
        assertEquals(LoadType.BAD, viewModel.loadType.value)
    }

    @Test
    fun `setLoadType toggles off if same type is selected`() {
        viewModel.setLoadType(LoadType.BAD)
        viewModel.setLoadType(LoadType.BAD)
        assertEquals(LoadType.NONE, viewModel.loadType.value)
    }

    @Test
    fun `stopLoad sets load type to NONE`() {
        viewModel.setLoadType(LoadType.BETTER)
        viewModel.stopLoad()
        assertEquals(LoadType.NONE, viewModel.loadType.value)
    }

    @Test
    fun `toggleInfoDialog changes showInfoDialog state`() {
        val initial = viewModel.showInfoDialog.value
        viewModel.toggleInfoDialog()
        assertEquals(!initial, viewModel.showInfoDialog.value)
    }

    @Test
    fun `resetStats calls frameMonitor reset and clears recompositionCount`() {
        viewModel.incrementRecompositionCount()
        viewModel.resetStats()
        verify(frameMonitor).reset()
        assertEquals(0, viewModel.recompositionCount.value)
    }

    @Test
    fun `incrementRecompositionCount increases the count`() {
        val initial = viewModel.recompositionCount.value
        viewModel.incrementRecompositionCount()
        assertEquals(initial + 1, viewModel.recompositionCount.value)
    }
}
