package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedDark
import com.example.ui.theme.MedicalRedSoft
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * مكون مرن لاختيار تاريخ القياس (اليوم، أمس، أو أي تاريخ سابق بالتقويم)
 */
@Composable
fun PastDatePickerRow(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    val effectiveLabel = if (label.isNotBlank()) label else "${strings.date}:"
    val context = LocalContext.current
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    val yesterday = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    val twoDaysAgo = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -2)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    val isPastDate = selectedDate != today

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = effectiveLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isPastDate) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${strings.addPastReading}: $selectedDate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Quick Selector Chips & DatePicker trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedDate == today,
                onClick = { onDateSelected(today) },
                label = { Text(strings.today, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MedicalRed,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedDate == yesterday,
                onClick = { onDateSelected(yesterday) },
                label = { Text(strings.yesterday, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MedicalRed,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedDate == twoDaysAgo,
                onClick = { onDateSelected(twoDaysAgo) },
                label = { Text(strings.twoDaysAgo, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MedicalRed,
                    selectedLabelColor = Color.White
                )
            )

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    try {
                        val parts = selectedDate.split("-")
                        if (parts.size == 3) {
                            cal.set(Calendar.YEAR, parts[0].toInt())
                            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
                            cal.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
                        }
                    } catch (_: Exception) {}

                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val c = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
                            onDateSelected(formatted)
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        // Prevent picking future dates
                        datePicker.maxDate = System.currentTimeMillis()
                    }.show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = strings.calendarPick,
                    tint = if (isPastDate && selectedDate != yesterday && selectedDate != twoDaysAgo) MedicalRed else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (selectedDate != today && selectedDate != yesterday && selectedDate != twoDaysAgo) selectedDate else strings.calendarPick,
                    fontSize = 11.sp,
                    color = if (isPastDate && selectedDate != yesterday && selectedDate != twoDaysAgo) MedicalRed else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isPastDate && selectedDate != yesterday && selectedDate != twoDaysAgo) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * مكون لاختيار توقيت القياس بدقة (سواء للقياس الحالي أو قياس سابق)
 */
@Composable
fun PastTimePickerRow(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    val effectiveLabel = if (label.isNotBlank()) label else "${strings.time}:"
    val context = LocalContext.current
    val nowTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    val quickTimes = remember(strings.isArabic) {
        if (strings.isArabic) {
            listOf("08:00 ص", "01:00 م", "06:00 م", "10:00 م")
        } else {
            listOf("08:00 AM", "01:00 PM", "06:00 PM", "10:00 PM")
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = effectiveLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val c = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }
                            val formatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(c.time)
                            onTimeSelected(formatted)
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = strings.selectTime,
                    tint = MedicalRed,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedTime.ifBlank { nowTime },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalRedDark
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = selectedTime == nowTime,
                    onClick = { onTimeSelected(nowTime) },
                    label = { Text(strings.currentTime, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicalRed,
                        selectedLabelColor = Color.White
                    )
                )
            }

            items(quickTimes) { t ->
                FilterChip(
                    selected = selectedTime == t,
                    onClick = { onTimeSelected(t) },
                    label = { Text(t, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicalRed,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}
