plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            export(libs.kotlinx.coroutines.core)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.koin.core)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.kotlinx.datetime)
        }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
    
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            linkerOpts.add("-lsqlite3")
        }
    }
}

android {
    namespace = "com.warriortech.resb.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

sqldelight {
    databases {
        create("ResbDatabase") {
            packageName.set("com.warriortech.resb.database")
            generateAsync.set(true)
        }
    }
}

tasks.register("packForXcode") {
    group = "build"
    description = "Builds framework for iOS"
    
    val buildType = System.getenv("CONFIGURATION")?.let { 
        if (it == "Debug") "Debug" else "Release" 
    } ?: "Debug"
    
    val targetName = System.getenv("SDK_NAME")?.let { sdk ->
        when {
            sdk.startsWith("iphoneos") -> "iosArm64"
            sdk.startsWith("iphonesimulator") -> {
                val arch = System.getenv("NATIVE_ARCH_ACTUAL") ?: "arm64"
                if (arch == "arm64") "iosSimulatorArm64" else "iosX64"
            }
            else -> "iosSimulatorArm64"
        }
    } ?: "iosSimulatorArm64"
    
    dependsOn("link${buildType}Framework${targetName.replaceFirstChar { it.uppercase() }}")
}
