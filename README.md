# 🚀 RealTimeFrameMonitoring

A Jetpack Compose demo application that visualizes UI performance, recomposition, and frame rendering in real time.  
This project demonstrates how different rendering strategies affect UI smoothness, responsiveness, and frame performance.

---

## ✨ Features

- 📈 Real-time FPS monitoring
- 🔁 Recomposition counter tracking
- 📉 Frame performance insights
- 🎯 Multiple rendering strategies comparison
- 📚 Built-in performance explanation dialog
- ⚡ Smooth and responsive Jetpack Compose UI

---

## 🎯 Performance Modes

### 🔴 Bad Mode
Heavy computation directly inside the draw phase causing:
- UI lag
- dropped frames
- poor responsiveness

### 🟡 Better Mode
Uses `remember` for caching computations:
- reduced recompositions
- improved rendering
- slight initial stutter

### 🟢 Best Mode
Uses `LaunchedEffect` with background processing:
- smooth rendering
- responsive UI
- optimized frame performance

---

## 🧠 Concepts Demonstrated

- Jetpack Compose recomposition
- Frame rendering lifecycle
- UI thread vs background thread
- Draw phase performance impact
- Efficient state management
- Using `remember` for caching
- Avoiding UI blocking operations

---

## 🛠️ Technologies Used

- Kotlin
- Jetpack Compose
- MVVM Architecture
- Coroutines
- State Management
- Material Design

---

## 🧪 Testing

### ✅ Unit Tests
- ViewModel state validation
- Mode switching logic
- Dialog visibility handling
- Recomposition counter updates
- Reset behavior validation

### ✅ UI Tests
- Initial UI rendering
- Mode selection flow
- Dialog interaction testing
- Compose UI behavior validation

---

## 🔧 Testing Tools

- JUnit
- Mockito
- Jetpack Compose UI Testing
- Kotlin Coroutines Test

---

## 🚀 Getting Started

### Clone the repository

```bash
git clone https://github.com/ithassan/RealTimeFrameMonitoring.git
```

### Open in Android Studio

1. Open Android Studio
2. Select Open Project
3. Sync Gradle files
4. Run the application

---

## 📸 Screenshots

None.

---

## 👨‍💻 Author

Hassan  
Android Developer

GitHub: https://github.com/ithassan

---

## ⭐ Support

If you like this project, consider giving it a star ⭐
