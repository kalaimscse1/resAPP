plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.ksp)
//    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.warriortech.resb"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.warriortech.resb"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.material3.lint)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
//    implementation(libs.esc.pos.usb.net.android)

    // WorkManager for synchronization
    implementation (libs.androidx.work.runtime.ktx)

    // DataStore for preferences
    implementation (libs.androidx.datastore.preferences)

    // Network connectivity monitoring
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
//    implementation(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.security.crypto.ktx)

    ksp(libs.room.compiler.v252)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android.core)
    implementation(libs.timber)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.v181)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.hilt.android.testing)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}