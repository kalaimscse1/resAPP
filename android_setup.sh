#!/bin/bash

# Android Development Setup Script for Replit
# This script sets up the necessary environment for Android development

echo "Setting up Android development environment..."

# Set environment variables
export ANDROID_HOME=$HOME/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Create SDK directory if it doesn't exist
mkdir -p $ANDROID_HOME

echo "Environment variables set:"
echo "ANDROID_HOME: $ANDROID_HOME"
echo "ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"

# Make gradlew executable
chmod +x ./gradlew

echo "Android development environment setup complete!"
echo "Note: Full Android SDK installation may require additional setup."
echo "This project is ready for development in an Android-capable environment."