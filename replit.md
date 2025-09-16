# resAPP - Restaurant Billing System

## Overview
A comprehensive Android restaurant billing system built with Kotlin, Jetpack Compose, Hilt dependency injection, and WorkManager. The system provides counter billing, table management, inventory tracking, payment processing, background data synchronization, and user support documentation.

## User Preferences
Preferred communication style: Simple, everyday language.

## Recent Changes (Latest: September 16, 2025)
- Successfully imported GitHub project into Replit environment
- **SECURITY**: Removed committed keystore file (key/signedkey.jks) and updated .gitignore to prevent future key commits
- Set up complete Android development environment with Java GraalVM 19.0.2
- Configured Gradle 8.11.1 with Kotlin 2.0.20 support
- Installed full Android SDK with required build tools and platform components
- Created Android development workflow and environment setup script
- Tested Android build process (completes most compilation tasks successfully)
- Established project structure for Android development in Replit

## Development Environment Setup
- **Java Runtime**: OpenJDK 19.0.2 with GraalVM CE 22.3.1
- **Build System**: Gradle 8.11.1 with Kotlin 2.0.20
- **Android Tools**: Basic android-tools package installed
- **Environment Script**: `android_setup.sh` for quick environment configuration

### Current Capabilities
- Java/Kotlin compilation environment ready (OpenJDK 19.0.2 with GraalVM CE)
- Gradle wrapper properly configured (Gradle 8.11.1)
- Full Android SDK installed with required components:
  - Android SDK Build-Tools 35
  - Android SDK Command-line Tools (latest)
  - Android SDK Platform-Tools
  - Android SDK Platform 35 (compileSdk target)
- Project dependencies and structure validated
- Android development workflow configured and tested

### Limitations
- Build process may encounter memory/resource limitations in Replit environment
- Android emulator not available in current environment
- Large builds may cause Gradle daemon to disappear due to resource constraints
- Requires manual environment setup for each session

### Building the Project
The Android project can be built using:
```bash
./android_setup.sh  # Set up Android SDK environment
./gradlew assembleDebug  # Build debug APK
```

**Note**: The build process successfully completes most Android compilation tasks but may encounter resource limitations during final assembly in the Replit environment.

## System Architecture
Android application structure:
- **Frontend**: Jetpack Compose with Material Design
- **Backend Logic**: Kotlin with Hilt dependency injection
- **Background Tasks**: WorkManager for data synchronization
- **Reports**: PDF generation using iTextPDF library
- **Database**: Local SQLite with API integration capabilities

Key components:
- Counter billing and quick billing screens
- Table management system
- Inventory tracking
- Payment processing (Cash, Card, UPI, Due)
- Comprehensive reporting (Sales, Itemwise, Category-wise)
- User support documentation with video tutorials

## External Dependencies
- Kotlin & Jetpack Compose for UI
- Hilt for dependency injection
- WorkManager for background tasks
- iTextPDF for report generation
- Apache POI for Excel exports
- Material Design components