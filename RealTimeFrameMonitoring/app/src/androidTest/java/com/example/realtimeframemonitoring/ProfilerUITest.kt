package com.example.realtimeframemonitoring

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ProfilerUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInitialUIState() {
        val frameMonitor = FrameMonitor()
        composeTestRule.setContent {
            ProfilerScreen(
                viewModel = ProfilerViewModel(frameMonitor),
                frameMonitor = frameMonitor
            )
        }

        // Check if header is displayed
        composeTestRule.onNodeWithText("Performance Lab").assertIsDisplayed()

        // Check if stats card items are displayed
        composeTestRule.onNodeWithText("FPS").assertIsDisplayed()
        composeTestRule.onNodeWithText("CPU").assertIsDisplayed()
        composeTestRule.onNodeWithText("RAM").assertIsDisplayed()

        // Check if initial instruction is shown
        composeTestRule.onNodeWithText("Select a mode above to start simulation").assertIsDisplayed()
    }

    @Test
    fun testModeSelectionAndStop() {
        val frameMonitor = FrameMonitor()
        composeTestRule.setContent {
            ProfilerScreen(
                viewModel = ProfilerViewModel(frameMonitor),
                frameMonitor = frameMonitor
            )
        }

        // Click on BAD mode
        composeTestRule.onNodeWithText("🔴 BAD").performClick()

        // Check if active mode status is shown and Stop button appears
        composeTestRule.onNodeWithText("🔴 BAD MODE").assertIsDisplayed()
        composeTestRule.onNodeWithText("STOP").assertIsDisplayed()

        // Click Stop
        composeTestRule.onNodeWithText("STOP").performClick()

        // Verify it returned to selection state
        composeTestRule.onNodeWithText("🔴 BAD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select a mode above to start simulation").assertIsDisplayed()
    }

    @Test
    fun testInfoDialog() {
        val frameMonitor = FrameMonitor()
        composeTestRule.setContent {
            ProfilerScreen(
                viewModel = ProfilerViewModel(frameMonitor),
                frameMonitor = frameMonitor
            )
        }

        // Click info icon (find by content description)
        composeTestRule.onNodeWithContentDescription("Info").performClick()

        // Check if dialog title is shown
        composeTestRule.onNodeWithText("Performance Classroom").assertIsDisplayed()
        
        // Check if specific lesson is visible
        composeTestRule.onNodeWithText("📊 RECOMPOSITION COUNT").assertIsDisplayed()

        // Dismiss dialog
        composeTestRule.onNodeWithText("Dismiss").performClick()

        // Verify dialog is gone
        composeTestRule.onNodeWithText("Performance Classroom").assertDoesNotExist()
    }
}
