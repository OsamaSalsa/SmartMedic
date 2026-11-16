package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// Design System: الهوية البصرية الطبية الموحدة (M3 Medical Design System)
// =========================================================================

// 1. اللون الأساسي الموحد (Primary Red - بدرجتين للفاتح والداكن لضمان التباين)
val MedicalRed = Color(0xFFDC2626)          // أحمر طبي أساسي معتمد للوضع الفاتح
val MedicalRedDark = Color(0xFFB91C1C)      // أحمر عميق للظلال والحالات النشطة
val MedicalRedDeep = Color(0xFF991B1B)      // أحمر داكن جداً
val MedicalRedLight = Color(0xFFEF4444)     // أحمر ساطع للعناصر البارزة
val MedicalRedSoft = Color(0xFFFEE2E2)      // خلفية خفيفة متناسقة (Tonal Light)
val MedicalRedDarkTheme = Color(0xFFF87171) // أحمر مخفف عالي التباين للوضع الداكن (WCAG AA)
val MedicalRedSoftDark = Color(0xFF451A1D)  // خلفية خفيفة للوضع الداكن (Tonal Dark)

// 2. اللون الثانوي المحايد (Neutral Secondary)
val NeutralSecondaryLight = Color(0xFF475569) // رمادي أردوازي داكن
val NeutralSecondaryDark = Color(0xFF94A3B8)  // رمادي أردوازي فاتح عالي التباين

// 3. التدرجات المعتمدة (Gradients)
val PrimaryRedGradient = Brush.horizontalGradient(
    listOf(MedicalRedDark, MedicalRed)
)
val AccentRedGradient = Brush.linearGradient(
    listOf(MedicalRed, MedicalRedLight)
)
val EmergencyRedGradient = Brush.horizontalGradient(
    listOf(Color(0xFFB91C1C), Color(0xFFDC2626))
)

// 4. ألوان الحالة الطبية الدلالية فقط (Medical Semantic Status)
// - الأخضر: القيم الطبيعية والصحية
val StatusNormal = Color(0xFF16A34A)
val StatusNormalDark = Color(0xFF4ADE80)
val StatusNormalContainerLight = Color(0xFFDCFCE7)
val StatusNormalContainerDark = Color(0xFF143823)

// - البرتقالي/العنبري: القيم المرتفعة قليلاً أو التنبيهات
val StatusWarning = Color(0xFFD97706)
val StatusWarningDark = Color(0xFFFBBF24)
val StatusWarningContainerLight = Color(0xFFFEF3C7)
val StatusWarningContainerDark = Color(0xFF3E2D07)

// - الأحمر: الحالات الحرجة أو القيم الخطرة
val StatusCritical = Color(0xFFDC2626)
val StatusCriticalDark = Color(0xFFF87171)
val StatusCriticalContainerLight = Color(0xFFFEE2E2)
val StatusCriticalContainerDark = Color(0xFF451216)

// - الأزرق الطبي: المتابعة والمعلومات الطبية العامة
val StatusInfo = Color(0xFF2563EB)
val StatusInfoDark = Color(0xFF60A5FA)
val StatusInfoContainerLight = Color(0xFFEFF6FF)
val StatusInfoContainerDark = Color(0xFF1E293B)

// توافقية الأسماء السابقة لدعم الكود القديم دون كسر
val SuccessGreen = StatusNormal
val SuccessGreenDark = Color(0xFF15803D)
val SuccessGreenSoft = StatusNormalContainerLight

val WarningAmber = StatusWarning
val WarningAmberSoft = StatusWarningContainerLight

val DangerRed = StatusCritical
val DangerRedSoft = StatusCriticalContainerLight

val InfoBlue = StatusInfo
val InfoBlueSoft = StatusInfoContainerLight

// 5. خلفيات ونصوص الوضع الفاتح (Light Mode - دافئ بدون توهج مزعج)
val BackgroundLight = Color(0xFFF8F9FA)     // خلفية بيضاء مائلة للدفء خفيفة على العين
val SurfaceLight = Color(0xFFFFFFFF)        // بطاقات بيضاء نقية بحدود وظل خفيف
val SurfaceVariantLight = Color(0xFFF1F5F9) // أسطح ثانوية وشارات
val TextPrimaryLight = Color(0xFF0F172A)    // نص رئيسي كحلي داكن (نسبة تباين > 12:1)
val TextSecondaryLight = Color(0xFF475569)  // نصوص ثانوية وتواريخ (نسبة تباين > 5.5:1)
val TextMutedLight = Color(0xFF64748B)      // نصوص صامتة داعمة للوضوح
val BorderLight = Color(0xFFE2E8F0)         // حدود ناعمة ومحددة

// 6. خلفيات ونصوص الوضع الداكن (Dark Mode - رمادي فحمي فاخر)
val BackgroundDark = Color(0xFF121212)      // رمادي فحمي مريح (ليس أسود نقي)
val SurfaceDark = Color(0xFF1E1E1E)         // بطاقات المستوى الأول
val SurfaceVariantDark = Color(0xFF262626)  // بطاقات المستوى الثاني والحاويات
val TextPrimaryDark = Color(0xFFF8FAFC)     // نص رئيسي فاتح جداً
val TextSecondaryDark = Color(0xFFCBD5E1)   // نص ثانوي فاتح واضح (نسبة تباين > 6:1)
val TextMutedDark = Color(0xFF94A3B8)       // نصوص صامتة
val BorderDark = Color(0xFF2D2D2D)          // حدود خافتة أنيقة
