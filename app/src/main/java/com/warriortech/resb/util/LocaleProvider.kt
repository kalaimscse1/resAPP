package com.warriortech.resb.util

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun LocaleProvider(
    content: @Composable (LocaleState) -> Unit
) {
    val context = LocalContext.current
    val currentLanguage = remember { LocaleHelper.getLanguage(context) }
    val currentLocale = remember { LocaleHelper.getCurrentLocale(context) }
    val isRTL = remember { LocaleHelper.isRTL(context) }
    val availableLanguages = remember { LocaleHelper.getAvailableLanguages() }

    val localeState = remember {
        LocaleState(
            currentLanguage = currentLanguage,
            currentLocale = currentLocale,
            isRTL = isRTL,
            availableLanguages = availableLanguages,
            onLanguageChange = { language ->
                LocaleHelper.setLocale(context, language)
            }
        )
    }

    content(localeState)
}

data class LocaleState(
    val currentLanguage: String,
    val currentLocale: Locale,
    val isRTL: Boolean,
    val availableLanguages: List<Pair<String, String>>,
    val onLanguageChange: (String) -> Unit
)

@Composable
fun rememberLocaleState(): LocaleState {
    val context = LocalContext.current
    return remember {
        LocaleState(
            currentLanguage = LocaleHelper.getLanguage(context),
            currentLocale = LocaleHelper.getCurrentLocale(context),
            isRTL = LocaleHelper.isRTL(context),
            availableLanguages = LocaleHelper.getAvailableLanguages(),
            onLanguageChange = { language ->
                LocaleHelper.setLocale(context, language)
            }
        )
    }
}
