package com.example.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val titleAr: String,
    val titleEn: String,
    val titleRu: String,
    val isRtl: Boolean
) {
    AUTO("auto", "تلقائي (لغة الجهاز)", "Auto (System Language)", "Автоматически (Язык системы)", false),
    AR("ar", "العربية", "Arabic", "Арабский", true),
    EN("en", "الإنجليزية", "English", "Английский", false),
    RU("ru", "الروسية", "Russian", "Русский", false);

    fun displayName(currentLang: AppLanguage = this): String = when (currentLang) {
        AR -> titleAr
        RU -> titleRu
        else -> titleEn
    }

    /**
     * Resolves the actual effective language when AUTO is selected by checking the device's default locale.
     */
    fun resolveEffectiveLanguage(): AppLanguage {
        if (this != AUTO) return this
        val deviceLang = Locale.getDefault().language.lowercase()
        return when {
            deviceLang.startsWith("ru") -> RU
            deviceLang.startsWith("ar") -> AR
            else -> EN
        }
    }

    val layoutDirection: LayoutDirection
        get() = if (this == AR) LayoutDirection.Rtl else LayoutDirection.Ltr
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.AR }
val LocalAppStrings = compositionLocalOf { AppStringsAr }
