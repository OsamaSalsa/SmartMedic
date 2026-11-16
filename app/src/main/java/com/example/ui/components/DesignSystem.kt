package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeStatus
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.EmergencyRedGradient
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedDark
import com.example.ui.theme.MedicalRedDarkTheme
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.theme.MedicalRedSoftDark
import com.example.ui.theme.PrimaryRedGradient
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusNormalDark
import com.example.ui.theme.StatusNormalContainerLight
import com.example.ui.theme.StatusNormalContainerDark
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningDark
import com.example.ui.theme.StatusWarningContainerLight
import com.example.ui.theme.StatusWarningContainerDark
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalDark
import com.example.ui.theme.StatusCriticalContainerLight
import com.example.ui.theme.StatusCriticalContainerDark
import com.example.ui.theme.isAppInDarkTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * زر رئيسي متدرج باللون الأحمر العصري (Design System 2026)
 * متوافق مع معايير الوصول (حجم اللمس الأدنى 48dp)
 */
@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    shapeRadius: Dp = 16.dp,
    testTag: String = "primary_gradient_btn"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val gradient = if (enabled) PrimaryRedGradient else Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8)))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp, max = height)
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = RoundedCornerShape(shapeRadius),
                ambientColor = MedicalRedDark.copy(alpha = 0.3f),
                spotColor = MedicalRed.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(shapeRadius))
            .background(gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                enabled = enabled,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * زر ثانوي أنيق بإطار ناعم وخلفية بيضاء
 */
@Composable
fun SecondaryOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    textColor: Color = MedicalRed,
    borderColor: Color = MedicalRed.copy(alpha = 0.4f),
    shapeRadius: Dp = 16.dp,
    height: Dp = 50.dp,
    testTag: String = "secondary_outline_btn"
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp, max = height)
            .testTag(testTag),
        shape = RoundedCornerShape(shapeRadius),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
        shadowElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * بطاقة زجاجية (Glassmorphism) خفيفة وعصرية للواجهات الطبية
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shapeRadius: Dp = 20.dp,
    elevation: Dp = 2.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .border(0.8.dp, borderColor, RoundedCornerShape(shapeRadius)),
        shape = RoundedCornerShape(shapeRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        content()
    }
}

/**
 * ترويسة الشاشات الموحدة (Unified Screen Header)
 * نمط موحد وأنيق لكل شاشات التطبيق يدعم الوضعين الفاتح والداكن
 */
@Composable
fun AppScreenHeader(
    title: String,
    subtitle: String? = null,
    badge: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    badge?.invoke()
                }
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = actions
                )
            }
        }
    }
}

/**
 * بطاقة الحالة الفارغة الموحدة (Empty State Card)
 */
@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionButton != null) {
                Spacer(modifier = Modifier.height(14.dp))
                actionButton()
            }
        }
    }
}

/**
 * بطاقة Bento Grid تفاعلية وموحدة الهوية
 */
@Composable
fun BentoCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color? = null,
    iconBackground: Color? = null,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    val isDark = isAppInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val resolvedIconTint = iconTint ?: primaryColor
    val resolvedIconBg = iconBackground ?: primaryColor.copy(alpha = if (isDark) 0.18f else 0.10f)

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(resolvedIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = resolvedIconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(primaryColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = primaryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * مؤشر مقياس نصف دائري احترافي (Speedometer Gauge)
 * تحسين سُمك القوس والألوان والمؤشر ووضوح الرقم المركزي بتدرج ناعم
 */
@Composable
fun SpeedometerGauge(
    currentValue: Float,
    hasValue: Boolean = currentValue > 0f,
    minValue: Float = 70f,
    maxValue: Float = 140f,
    unit: String = "mg/dL",
    title: String = "",
    statusText: String = "",
    statusColor: Color = StatusNormal,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    val effectiveTitle = if (title.isNotBlank()) title else strings.latestSugarReading
    val effectiveStatus = if (!hasValue) strings.noReadingsToday else if (statusText.isNotBlank()) strings.localizeStatus(statusText) else strings.normal
    val progress = if (hasValue) ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900),
        label = "gauge_progress"
    )

    val isDark = isAppInDarkTheme()
    val needleColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val trackBgColor = if (isDark) Color(0xFF262626) else Color(0xFFE2E8F0)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = effectiveTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = statusColor.copy(alpha = if (isDark) 0.22f else 0.12f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = effectiveStatus,
                        color = statusColor,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 130.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val strokeWidth = 16.dp.toPx()
                    val radius = (size.width - strokeWidth) / 2
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(radius * 2, radius * 2)

                    // 1. Full Background Track (Soft Neutral)
                    drawArc(
                        color = trackBgColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Colored Semantic Arcs: Normal (Green), Warning (Amber), Critical (Red)
                    val sweepUnit = 60f
                    // Green Zone
                    drawArc(
                        color = StatusNormal,
                        startAngle = 180f,
                        sweepAngle = sweepUnit,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Amber Zone
                    drawArc(
                        color = StatusWarning,
                        startAngle = 180f + sweepUnit,
                        sweepAngle = sweepUnit,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    // Red Zone
                    drawArc(
                        color = StatusCritical,
                        startAngle = 180f + (sweepUnit * 2),
                        sweepAngle = sweepUnit,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 3. Precision Pointer / Needle
                    val currentAngle = 180f + (animatedProgress * 180f)
                    val radians = Math.toRadians(currentAngle.toDouble())
                    val needleLength = radius - 8.dp.toPx()
                    val centerOffset = Offset(size.width / 2, size.height / 2)
                    val needleEnd = Offset(
                        (centerOffset.x + needleLength * cos(radians)).toFloat(),
                        (centerOffset.y + needleLength * sin(radians)).toFloat()
                    )

                    drawLine(
                        color = needleColor,
                        start = centerOffset,
                        end = needleEnd,
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = needleColor,
                        radius = 7.dp.toPx(),
                        center = centerOffset
                    )
                    drawCircle(
                        color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                        radius = 3.5.dp.toPx(),
                        center = centerOffset
                    )
                }

                // Central Large Bold Number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = if (hasValue) "${currentValue.toInt()}" else "--",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = unit,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Semantic Legends Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusNormal))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(strings.normal, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusWarning))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(strings.high, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusCritical))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(strings.criticalDanger, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/**
 * شارة الحالة الدلالية (Status Badge)
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    val isDark = isAppInDarkTheme()
    val displayStatus = strings.localizeStatus(status)
    val lower = status.lowercase().trim()
    val isNeutral = lower in listOf(
        "لا قراءات", "لا توجد قراءات", "لم تسجل قراءات", "لم تسجل قراءات اليوم بعد",
        "no readings", "no readings logged today", "нет данных", "сегодня замеры еще не заносились",
        "لا يوجد", "--", "none", "غير محدد", "غير مسجل", "empty", "ملغى", "cancelled"
    ) || lower.startsWith("bmi")

    val (bgColor, textColor) = when {
        isNeutral ->
            Pair(
                if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9),
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        lower in listOf("طبيعي", "مكتمل", "normal", "جيد", "سليم", "норма", "в норме") ->
            Pair(
                if (isDark) StatusNormalContainerDark else StatusNormalContainerLight,
                if (isDark) StatusNormalDark else StatusNormal
            )
        lower in listOf("زائد", "مرتفع", "high", "أزمة", "alert", "خطر", "حرجة", "высокий", "критический") ->
            Pair(
                if (isDark) StatusCriticalContainerDark else StatusCriticalContainerLight,
                if (isDark) StatusCriticalDark else StatusCritical
            )
        lower in listOf("نقص", "منخفض", "low", "مرتفع طفيف", "مرحلة 1", "مرحلة 2", "تنبيه", "понижен") ->
            Pair(
                if (isDark) StatusWarningContainerDark else StatusWarningContainerLight,
                if (isDark) StatusWarningDark else StatusWarning
            )
        lower in listOf("قادم", "upcoming", "scheduled") ->
            Pair(
                if (isDark) MedicalRedSoftDark else MedicalRedSoft,
                if (isDark) MedicalRedDarkTheme else MedicalRed
            )
        else ->
            Pair(
                if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9),
                MaterialTheme.colorScheme.onSurfaceVariant
            )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isNeutral) MaterialTheme.colorScheme.outline.copy(alpha = 0.35f) else textColor.copy(alpha = 0.3f)
        ),
        modifier = modifier
    ) {
        Text(
            text = displayStatus,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/**
 * شارة مستوى الفحص الطبي (طبيعي / زائد / نقص) بتمييز لوني واضح وعصري
 */
@Composable
fun TestLevelBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    val isDark = isAppInDarkTheme()
    val bgColor: Color
    val textColor: Color
    val symbol: String
    val lower = level.trim().lowercase()

    val displayLevel = strings.localizeStatus(level)

    when {
        lower in listOf("normal", "طبيعي", "طبيعية", "جيد", "سليم", "норма", "в норме") -> {
            bgColor = if (isDark) Color(0xFF143823) else Color(0xFFDCFCE7)
            textColor = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)
            symbol = "✓"
        }
        lower in listOf("high", "زائد", "مرتفع", "عالي", "ارتفاع", "повышен", "высокий") -> {
            bgColor = if (isDark) Color(0xFF451216) else Color(0xFFFEE2E2)
            textColor = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C)
            symbol = "↑"
        }
        lower in listOf("low", "نقص", "منخفض", "انخفاض", "هبوط", "понижен", "низкий") -> {
            bgColor = if (isDark) Color(0xFF3E2D07) else Color(0xFFFEF3C7)
            textColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
            symbol = "↓"
        }
        else -> {
            bgColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
            textColor = MaterialTheme.colorScheme.onSurfaceVariant
            symbol = "•"
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = symbol,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${strings.status}: $displayLevel",
                color = textColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * حقل إدخال عصري (Design System 2026) بزوايا دائرية 14dp
 */
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    singleLine: Boolean = true,
    testTag: String = "modern_text_field"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder, fontSize = 13.sp) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null, tint = MedicalRed.copy(alpha = 0.8f)) }
        } else null,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(14.dp),
        singleLine = singleLine,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedicalRed,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MedicalRed,
            cursorColor = MedicalRed
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

/**
 * زر استشارة طارئة عائم ومميز (Emergency Floating Banner / FAB)
 */
@Composable
fun UrgentConsultationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0xFFDC2626).copy(alpha = 0.4f),
                spotColor = Color(0xFFEF4444).copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(EmergencyRedGradient)
            .clickable(onClick = onClick)
            .testTag("urgent_consultation_floating_btn"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = strings.emergencyQuickConsult,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("24/7", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
