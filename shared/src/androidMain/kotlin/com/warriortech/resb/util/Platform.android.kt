package com.warriortech.resb.util

import android.os.Build

actual class Platform actual constructor() {
    actual val name: String = "Android ${Build.VERSION.SDK_INT}"
    actual val version: String = Build.VERSION.RELEASE
}

actual fun getPlatformName(): String = "Android"
