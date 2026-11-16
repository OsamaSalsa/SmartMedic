package com.example.ui.theme

import android.graphics.Typeface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.example.R

// الوضع الداكن (Dark Mode - رمادي فحمي فاخر مريح للعين)
private val MedicalDarkColorScheme = darkColorScheme(
    primary = MedicalRedDarkTheme,
    onPrimary = Color.Black,
    primaryContainer = MedicalRedSoftDark,
    onPrimaryContainer = Color(0xFFFFD9DC),
    secondary = NeutralSecondaryDark,
    onSecondary = Color.Black,
    tertiary = StatusNormalDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = Color(0xFF262626),
    error = StatusCriticalDark,
    onError = Color.Black
)

// الوضع الفاتح (Light Mode - ناعم، دافئ وأنيق)
private val MedicalLightColorScheme = lightColorScheme(
    primary = MedicalRed,
    onPrimary = Color.White,
    primaryContainer = MedicalRedSoft,
    onPrimaryContainer = MedicalRedDeep,
    secondary = NeutralSecondaryLight,
    onSecondary = Color.White,
    tertiary = StatusNormal,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = Color(0xFFF1F5F9),
    error = StatusCritical,
    onError = Color.White
)

val MedicalShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),      // أزرار، حقول إدخال، شارات
    large = RoundedCornerShape(16.dp),       // بطاقات رئيسية، حاويات
    extraLarge = RoundedCornerShape(20.dp)   // بطاقات بارزة، نوافذ منبثقة
)

val LocalIsDarkMode = androidx.compose.runtime.staticCompositionLocalOf { false }

@Composable
fun isAppInDarkTheme(): Boolean {
    val localDark = LocalIsDarkMode.current
    return localDark || (MaterialTheme.colorScheme.surface.luminance() < 0.5f)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isArabic: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = if (darkTheme) MedicalDarkColorScheme else MedicalLightColorScheme

    val typography = remember(context, isArabic) {
        if (!isArabic) {
            DefaultTypography
        } else {
            val safeCairoFamily = try {
                val baseTypeface = ResourcesCompat.getFont(context, R.font.cairo)
                if (baseTypeface != null) {
                    FontFamily(Typeface.create(baseTypeface, Typeface.BOLD))
                } else {
                    FontFamily.Default
                }
            } catch (_: Throwable) {
                FontFamily.Default
            }
            buildArabicTypography(safeCairoFamily)
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalIsDarkMode provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = MedicalShapes,
            content = content
        )
    }
}
