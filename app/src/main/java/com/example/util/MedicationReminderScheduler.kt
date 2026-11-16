package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import com.example.MainActivity
import com.example.data.model.MedicationEntity
import com.example.receiver.MedicationAlarmReceiver
import java.util.Calendar

object MedicationReminderScheduler {

    const val CHANNEL_ID = "medication_reminders_channel"

    fun createNotificationChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val prefs = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
            val code = prefs.getString("selected_language_code", com.example.ui.locale.AppLanguage.AUTO.code) ?: com.example.ui.locale.AppLanguage.AUTO.code
            val lang = (com.example.ui.locale.AppLanguage.entries.firstOrNull { it.code == code } ?: com.example.ui.locale.AppLanguage.AUTO).resolveEffectiveLanguage()

            val channelName = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "تذكيرات مواعيد الأدوية"
                com.example.ui.locale.AppLanguage.RU -> "Напоминания о приёме лекарств"
                else -> "Medication Reminders"
            }
            val channelDesc = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "إشعارات يومية لتذكير المستخدم بمواعيد تناول الأدوية والجرعات"
                com.example.ui.locale.AppLanguage.RU -> "Ежедневные уведомления о времени приёма лекарств и дозировках"
                else -> "Daily notifications to remind you of scheduled medication doses"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDesc
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                enableLights(true)
                lightColor = android.graphics.Color.RED
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(context: Context, med: MedicationEntity, patientName: String = "") {
        if (!med.isActive) {
            cancelReminder(context, med.id)
            return
        }

        createNotificationChannelIfNeeded(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra("med_id", med.id)
            putExtra("med_name", med.name)
            putExtra("med_dosage", med.dosage)
            putExtra("med_instructions", med.instructions)
            putExtra("patient_name", patientName)
            putExtra("med_hour", med.hour)
            putExtra("med_minute", med.minute)
            putExtra("med_frequency", med.frequency)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            med.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, med.hour)
            set(Calendar.MINUTE, med.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val triggerAtMillis = calendar.timeInMillis
        setExactAlarm(alarmManager, triggerAtMillis, pendingIntent, context)
    }

    private fun setExactAlarm(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
        context: Context
    ) {
        if (!canScheduleExactAlarms(context)) {
            fallbackSchedule(alarmManager, triggerAtMillis, pendingIntent)
            return
        }
        try {
            val showIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                0,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // setAlarmClock is an exact alarm mechanism that works when SCHEDULE_EXACT_ALARM is granted
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d("MedicationScheduler", "Scheduled exact AlarmClock for $triggerAtMillis")
        } catch (e: Exception) {
            Log.e("MedicationScheduler", "Error setting AlarmClock, using fallback exact method", e)
            fallbackSchedule(alarmManager, triggerAtMillis, pendingIntent)
        }
    }

    private fun fallbackSchedule(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e("MedicationScheduler", "Cannot schedule exact alarm, falling back to inexact set", e)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancelReminder(context: Context, medId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MedicationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun triggerInstantNotification(context: Context, med: MedicationEntity, patientName: String = "") {
        createNotificationChannelIfNeeded(context)
        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra("med_id", med.id)
            putExtra("med_name", med.name)
            putExtra("med_dosage", med.dosage)
            putExtra("med_instructions", med.instructions)
            putExtra("patient_name", patientName)
            putExtra("med_hour", med.hour)
            putExtra("med_minute", med.minute)
            putExtra("med_frequency", med.frequency)
        }
        context.sendBroadcast(intent)
    }

    fun rescheduleAllActiveReminders(context: Context, medications: List<MedicationEntity>, patientName: String) {
        createNotificationChannelIfNeeded(context)
        val activeMeds = medications.filter { it.isActive }
        for (med in activeMeds) {
            scheduleReminder(context, med, patientName)
        }
        Log.d("MedicationScheduler", "Rescheduled ${activeMeds.size} active medication alarms.")
    }

    /**
     * جدولة موعد التنبيه لليوم التالي تلقائياً لضمان استمرار عمل التنبيهات بالخلفية
     */
    fun scheduleNextDailyOccurrence(
        context: Context,
        medId: Long,
        hour: Int,
        minute: Int,
        name: String,
        dosage: String,
        instructions: String,
        patientName: String,
        frequency: String
    ) {
        createNotificationChannelIfNeeded(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra("med_id", medId)
            putExtra("med_name", name)
            putExtra("med_dosage", dosage)
            putExtra("med_instructions", instructions)
            putExtra("patient_name", patientName)
            putExtra("med_hour", hour)
            putExtra("med_minute", minute)
            putExtra("med_frequency", frequency)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val triggerAtMillis = calendar.timeInMillis
        setExactAlarm(alarmManager, triggerAtMillis, pendingIntent, context)
    }

    /**
     * Checks if exact alarms are allowed on Android 12+ (API 31+)
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            true
        }
    }

    /**
     * Checks if notifications are enabled for the app
     */
    fun areNotificationsEnabled(context: Context): Boolean {
        return androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * Checks if app is exempted from battery optimizations (Doze mode)
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    }

    /**
     * Opens system exact alarm permission settings on Android 12+
     */
    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                openAppNotificationSettings(context)
            }
        }
    }

    /**
     * Opens system app notification settings
     */
    fun openAppNotificationSettings(context: Context) {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${context.packageName}".toUri()
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    /**
     * Opens system battery optimization list (zero-permission Google Play compliant intent)
     */
    fun openBatteryOptimizationSettings(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppNotificationSettings(context)
        }
    }
}
