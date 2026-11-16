package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.isAppInDarkTheme

enum class NavItem(
    val title: String,
    val icon: ImageVector,
    val tag: String
) {
    HOME("Home", Icons.Default.Home, "nav_home"),
    DAILY("Daily Tracking", Icons.Default.Favorite, "nav_daily"),
    RESULTS("Lab Results", Icons.Default.Biotech, "nav_results"),
    AI_ADVISOR("AI Advisor", Icons.Default.AutoAwesome, "nav_ai_advisor"),
    PROFILE("Profile", Icons.Default.Person, "nav_profile")
}

fun NavItem.getLocalizedTitle(strings: com.example.ui.locale.AppStrings): String = when (this) {
    NavItem.HOME -> strings.navHome
    NavItem.DAILY -> strings.navDaily
    NavItem.RESULTS -> strings.navResults
    NavItem.AI_ADVISOR -> strings.navAiAdvisor
    NavItem.PROFILE -> strings.navProfile
}

@Composable
fun FloatingBottomNavigation(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        val isDark = isAppInDarkTheme()
        val primaryColor = MaterialTheme.colorScheme.primary

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = if (isDark) 4.dp else 10.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem.values().forEach { item ->
                    val isSelected = selectedItem == item
                    val localizedTitle = item.getLocalizedTitle(strings)

                    val activeBgColor by animateColorAsState(
                        targetValue = if (isSelected) primaryColor.copy(alpha = if (isDark) 0.20f else 0.12f) else Color.Transparent,
                        animationSpec = tween(durationMillis = 200),
                        label = "pill_bg"
                    )
                    val activeContentColor by animateColorAsState(
                        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "pill_content"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onItemSelected(item) }
                            .padding(vertical = 4.dp)
                            .testTag(item.tag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Active Capsule Pill around Icon
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(activeBgColor)
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = localizedTitle,
                                    tint = activeContentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = localizedTitle,
                                color = activeContentColor,
                                fontSize = if (isSelected) 10.5.sp else 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
