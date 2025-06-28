
package com.warriortech.resb.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun getScreenSizeInfo(): ScreenSizeInfo {
    val configuration = LocalConfiguration.current
    return ScreenSizeInfo(
        hPX = configuration.screenHeightDp.dp,
        wPX = configuration.screenWidthDp.dp
    )
}

data class ScreenSizeInfo(
    val hPX: Dp,
    val wPX: Dp
) {
    val isCompact: Boolean get() = wPX < 600.dp
    val isMedium: Boolean get() = wPX >= 600.dp && wPX < 840.dp
    val isExpanded: Boolean get() = wPX >= 840.dp
    val isLandscape: Boolean get() = wPX > hPX
}

object Dimensions {
    // Touch targets (minimum 48dp for accessibility)
    val touchTargetMin = 48.dp
    val touchTargetComfortable = 56.dp
    
    // Spacing
    val spacingXS = 4.dp
    val spacingS = 8.dp
    val spacingM = 16.dp
    val spacingL = 24.dp
    val spacingXL = 32.dp
    
    // Padding for different screen sizes
    @Composable
    fun getHorizontalPadding(): Dp {
        val screenInfo = getScreenSizeInfo()
        return when {
            screenInfo.isCompact -> spacingM
            screenInfo.isMedium -> spacingL
            else -> spacingXL
        }
    }
    
    @Composable
    fun getVerticalPadding(): Dp {
        val screenInfo = getScreenSizeInfo()
        return when {
            screenInfo.isCompact -> spacingS
            else -> spacingM
        }
    }
}
