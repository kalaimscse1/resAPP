# resAPP - Restaurant Billing System

## Overview
A comprehensive Android restaurant billing system built with Kotlin, Jetpack Compose, Hilt dependency injection, and WorkManager. The system provides counter billing, table management, inventory tracking, payment processing, background data synchronization, and user support documentation.

## User Preferences
Preferred communication style: Simple, everyday language.

## Recent Changes (Latest: September 08, 2025)
- Fixed amount formatting in PDF reports to display exactly 2 decimal places for all monetary values
- Updated itemwise report PDF to show proper currency formatting (₹123.45 instead of ₹123.4500000)
- Applied consistent decimal formatting across all report types (sales, itemwise, category-wise)
- Established proper Java development environment for Android compilation

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