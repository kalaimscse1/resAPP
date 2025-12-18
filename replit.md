# resAPP - Restaurant Billing System (Kotlin Multiplatform)

## Overview
A comprehensive restaurant billing system built with Kotlin Multiplatform, enabling code sharing across Android and iOS platforms. Features counter billing, table management, inventory tracking, payment processing, background data synchronization, and user support documentation.

## User Preferences
Preferred communication style: Simple, everyday language.

## Recent Changes (Latest: December 18, 2025)
- **KOTLIN MULTIPLATFORM CONVERSION**: Major architectural refactoring
  - Created shared module for cross-platform code (commonMain, androidMain, iosMain)
  - Migrated data models to shared module with kotlinx.serialization
  - Implemented Ktor-based network layer for multiplatform networking
  - Created SQLDelight database schema for offline-first architecture
  - Set up Koin dependency injection for shared code
  - Created iOS app structure with SwiftUI views
  - Updated Android app to use shared module
  - GitHub repository created: https://github.com/kalaimscse1/resB-multiplatform

- **KMP Dependencies Added**:
  - Ktor 2.3.7 (HTTP client)
  - SQLDelight 2.0.1 (Database)
  - Koin 3.5.3 (Dependency Injection)
  - kotlinx-serialization 1.6.2 (JSON parsing)
  - kotlinx-datetime 0.5.0 (Date/Time utilities)

## Previous Changes (December 16, 2025)
- **UI/UX FIXES**: Fixed 6 reported issues in the billing system
- **IMPORT COMPLETED**: Successfully migrated project to Replit environment

## Project Structure (Kotlin Multiplatform)
```
ResBMultiplatform/
├── app/                          # Android app module
│   ├── src/main/
│   │   ├── java/com/warriortech/resb/
│   │   │   ├── screens/          # Jetpack Compose screens
│   │   │   ├── ui/               # Theme, components, viewmodels
│   │   │   ├── data/             # Local database (Room - legacy)
│   │   │   └── ...
│   │   └── res/                  # Android resources
│   └── build.gradle.kts
├── shared/                       # Shared KMP module
│   ├── src/
│   │   ├── commonMain/kotlin/    # Platform-agnostic code
│   │   │   ├── model/            # Data classes with serialization
│   │   │   ├── network/          # Ktor API client
│   │   │   ├── repository/       # Business logic repositories
│   │   │   ├── di/               # Koin modules
│   │   │   └── util/             # Utilities
│   │   ├── androidMain/kotlin/   # Android-specific implementations
│   │   └── iosMain/kotlin/       # iOS-specific implementations
│   └── build.gradle.kts
├── iosApp/                       # iOS app module
│   ├── iosApp/
│   │   ├── ContentView.swift     # SwiftUI views
│   │   ├── iOSApp.swift          # App entry point
│   │   └── Info.plist
│   └── iosApp.xcodeproj/
├── gradle/libs.versions.toml     # Version catalog
├── settings.gradle.kts
└── build.gradle.kts
```

## KMP Architecture

### Shared Module
- **Models**: Serializable data classes for API communication
- **Network**: Ktor-based HTTP client with platform-specific engines
- **Database**: SQLDelight for offline-first data persistence
- **Repositories**: Business logic with Flow-based reactive data
- **DI**: Koin modules for dependency injection

### Platform-Specific Code
- **Android**: Ktor Android engine, SQLDelight Android driver
- **iOS**: Ktor Darwin engine, SQLDelight Native driver

## Development Environment Setup
- **Java Runtime**: OpenJDK 19.0.2 with GraalVM CE 22.3.1
- **Build System**: Gradle 8.11.1 with Kotlin 2.0.21
- **Android SDK**: Platform 35, Build Tools 35

### Building the Project
```bash
# Android
./android_setup.sh
./gradlew :app:assembleDebug

# Shared module
./gradlew :shared:build

# iOS (requires Xcode on macOS)
# Open iosApp/iosApp.xcodeproj in Xcode
```

## System Architecture
- **Frontend**: 
  - Android: Jetpack Compose with Material Design
  - iOS: SwiftUI with native components
- **Shared Logic**: Kotlin Multiplatform
- **Networking**: Ktor with kotlinx.serialization
- **Database**: SQLDelight (multiplatform)
- **DI**: Koin (multiplatform)
- **Background Tasks**: WorkManager (Android), BackgroundTasks (iOS)

## Key Components
- Counter billing and quick billing screens
- Table management system
- Kitchen order ticket (KOT) management
- Inventory tracking
- Payment processing (Cash, Card, UPI, Due)
- Paid bills management system
- Comprehensive reporting (Sales, Itemwise, Category-wise)
- Multi-language support (English, Tamil)

## External Dependencies
### Shared (KMP)
- Ktor for networking
- SQLDelight for database
- Koin for dependency injection
- kotlinx-serialization for JSON
- kotlinx-datetime for date/time

### Android-Specific
- Jetpack Compose for UI
- Hilt for legacy DI (migrating to Koin)
- WorkManager for background tasks
- iTextPDF for report generation
- Apache POI for Excel exports

### iOS-Specific
- SwiftUI for UI
- Native iOS frameworks
