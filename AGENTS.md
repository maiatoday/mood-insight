# Project Guidelines

This project follows modern Android development practices.

## Tech Stack
- **UI Toolkit**: Jetpack Compose
- **Navigation**: Navigation 3
- **Design System**: Material 3

## Architecture
Follow the official Android Architecture recommendations: https://developer.android.com/topic/architecture/recommendations

### Key Principles
- **Separation of Concerns**: distinct UI, Domain, and Data layers.
- **Drive UI from Data Models**: persistent models drive the UI state.
- **Single Source of Truth**: ensure data consistency.
- **Unidirectional Data Flow (UDF)**: state flows down, events flow up.

### Implementation Details
- Use **Kotlin Coroutines** and **Flow** for asynchronous operations.
- Use **ViewModels** to manage UI state.
- Use **Repository pattern** for data access.
