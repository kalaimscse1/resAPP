package com.warriortech.resb.util

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

object MobileUtils {
    
    fun isTablet(context: Context): Boolean {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val density = displayMetrics.density
        val dpHeight = displayMetrics.heightPixels / density
        val dpWidth = displayMetrics.widthPixels / density
        
        return dpWidth >= 600 && dpHeight >= 960
    }
    
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    
    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }
}

@Composable
fun pxToDp(px: Int): Dp {
    val density = LocalDensity.current
    return with(density) { px.toDp() }
}

@Composable
fun dpToPx(dp: Dp): Float {
    val density = LocalDensity.current
    return with(density) { dp.toPx() }
}

@Composable
fun getDeviceInfo(): DeviceInfo {
    val context = LocalContext.current
    return DeviceInfo(
        isTablet = MobileUtils.isTablet(context),
        isLandscape = MobileUtils.isLandscape(context),
        density = MobileUtils.getScreenDensity(context)
    )
}

data class DeviceInfo(
    val isTablet: Boolean,
    val isLandscape: Boolean,
    val density: Float
)
