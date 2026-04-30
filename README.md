🚀 RealTimeFrameMonitoring

A Jetpack Compose demo app that visualizes UI performance, recomposition, and frame rendering in real time. It compares different rendering strategies to demonstrate how heavy computations impact UI smoothness and responsiveness.

✨ Overview

This project is built as a performance learning lab for Jetpack Compose. It helps developers understand how rendering works under the hood and how poor decisions can lead to dropped frames and laggy UI.

📊 Features
📈 Real-time FPS monitoring
🔁 Recomposition counter tracking UI updates
📉 Frame performance insights (smooth vs janky frames)
🎯 Three performance modes:
🔴 Bad Mode
Heavy computation in the draw phase → causes lag and frame drops
🟡 Better Mode
Uses remember for caching → improves performance but may stutter initially
🟢 Best Mode
Uses LaunchedEffect with background threads → smooth and responsive UI
📚 Built-in Info Dialog explaining performance concepts
🧠 Key Concepts Demonstrated
Jetpack Compose recomposition
UI thread vs background thread work
Performance impact of draw phase operations
Efficient state management
Using remember for caching
Avoiding UI blocking operations
🧪 Testing
✅ Unit Tests (ViewModel)

Ensures correct business logic and state handling:

Default state validation
Load mode switching and toggling
Dialog visibility state
Recomposition counter updates
Reset behavior with FrameMonitor
✅ UI Tests (Compose)

Validates user interactions and UI behavior:

Initial UI rendering
Mode selection and stopping flow
Info dialog display and dismissal
🔧 Testing Tools
JUnit
Mockito
Jetpack Compose UI Testing
Kotlin Coroutines Test
