# resAPP - Restaurant Billing System

## Overview
A comprehensive Android restaurant billing system built with Kotlin, Jetpack Compose, Hilt dependency injection, and WorkManager. The system provides counter billing, table management, inventory tracking, payment processing, background data synchronization, and user support documentation.

## User Preferences
Preferred communication style: Simple, everyday language.

## Recent Changes (Latest: September 16, 2025)
- Successfully imported GitHub project into Replit environment
- Set up Android development environment with Java GraalVM 19.0.2
- Configured Gradle 8.11.1 with Kotlin 2.0.20 support
- Created Android development workflow and environment setup script
- Established project structure for Android development in Replit

## Development Environment Setup
- **Java Runtime**: OpenJDK 19.0.2 with GraalVM CE 22.3.1
- **Build System**: Gradle 8.11.1 with Kotlin 2.0.20
- **Android Tools**: Basic android-tools package installed
- **Environment Script**: `android_setup.sh` for quick environment configuration

### Current Capabilities
- Java/Kotlin compilation environment ready
- Gradle wrapper properly configured
- Project dependencies and structure validated
- Android development workflow configured

### Limitations
- Full Android SDK not installed (requires additional setup for APK building)
- Android emulator not available in current environment
- Build tasks may require Android SDK components for full compilation

### Building the Project
To build the Android APK when Android SDK is available:
```bash
./android_setup.sh  # Set up environment
./gradlew assembleDebug  # Build debug APK
```

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