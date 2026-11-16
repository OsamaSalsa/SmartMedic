package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.MedicalDatabase
import com.example.util.MedicationReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedicationAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        val prefs = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("selected_language_code", com.example.ui.locale.AppLanguage.AUTO.code) ?: com.example.ui.locale.AppLanguage.AUTO.code
        val lang = (com.example.ui.locale.AppLanguage.entries.firstOrNull { it.code == langCode } ?: com.example.ui.locale.AppLanguage.AUTO).resolveEffectiveLanguage()

        // Handle direct notification action "Mark Medication as Taken"
        if (action == "com.example.ACTION_MARK_TAKEN") {
            val defaultMedLabel = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "الدواء"
                com.example.ui.locale.AppLanguage.RU -> "лекарство"
                else -> "medication"
            }
            val medId = intent.getLongExtra("med_id", -1L)
            val medName = intent.getStringExtra("med_name") ?: defaultMedLabel
            if (medId != -1L) {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = MedicalDatabase.getDatabase(context)
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val med = db.medicalDao().getAllMedicationsList().firstOrNull { it.id == medId }
                        if (med != null) {
                            val updated = med.copy(lastTakenDate = todayStr)
                            db.medicalDao().updateMedication(updated)
                        }

                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                        notificationManager?.cancel(medId.toInt())

                        val toastMsg = when (lang) {
                            com.example.ui.locale.AppLanguage.AR -> "تم تسجيل تناول دواء $medName بنجاح! بالشفاء والعافية."
                            com.example.ui.locale.AppLanguage.RU -> "Приём препарата $medName успешно отмечен!"
                            else -> "Marked $medName as taken successfully!"
                        }
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            return
        }

        // Regular Medication Notification Trigger
        val medId = intent.getLongExtra("med_id", System.currentTimeMillis())
        // Acquire WakeLock to wake CPU and ensure notification and sound play reliably on Android 12+
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
        val wakeLock = powerManager?.newWakeLock(
            android.os.PowerManager.PARTIAL_WAKE_LOCK,
            "SmartMedic:MedicationAlarmWakeLock"
        )
        wakeLock?.acquire(10_000L) // Safe 10s auto-release
        try {
            val defaultMedName = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "الدواء المجدول"
                com.example.ui.locale.AppLanguage.RU -> "Назначенный препарат"
                else -> "Scheduled Medication"
            }
            val defaultDosage = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "الجرعة المحددة"
                com.example.ui.locale.AppLanguage.RU -> "Указанная дозировка"
                else -> "Prescribed dosage"
            }
            val defaultInstr = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "تناول الدواء الآن"
                com.example.ui.locale.AppLanguage.RU -> "Примите лекарство сейчас"
                else -> "Take medication now"
            }

            val medName = intent.getStringExtra("med_name") ?: defaultMedName
            val medDosage = intent.getStringExtra("med_dosage") ?: defaultDosage
            val medInstructions = intent.getStringExtra("med_instructions") ?: defaultInstr
            val patientName = intent.getStringExtra("patient_name") ?: ""

            val channelId = MedicationReminderScheduler.CHANNEL_ID
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            // Ensure Channel Created for Android 8+
            MedicationReminderScheduler.createNotificationChannelIfNeeded(context)

            // Tap Intent to open app
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("open_tab", "medications")
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                medId.toInt(),
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Action Intent to Mark as Taken directly from notification
            val markTakenIntent = Intent(context, MedicationAlarmReceiver::class.java).apply {
                this.action = "com.example.ACTION_MARK_TAKEN"
                putExtra("med_id", medId)
                putExtra("med_name", medName)
            }
            val markTakenPendingIntent = PendingIntent.getBroadcast(
                context,
                (medId + 100000).toInt(),
                markTakenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val title = if (patientName.isNotBlank()) {
                when (lang) {
                    com.example.ui.locale.AppLanguage.AR -> "تذكير دواء للمريض $patientName: $medName"
                    com.example.ui.locale.AppLanguage.RU -> "Напоминание ($patientName): $medName"
                    else -> "Medication Reminder ($patientName): $medName"
                }
            } else {
                when (lang) {
                    com.example.ui.locale.AppLanguage.AR -> "حان موعد تناول دواء: $medName"
                    com.example.ui.locale.AppLanguage.RU -> "Время принять лекарство: $medName"
                    else -> "Time to take medication: $medName"
                }
            }

            val contentText = "$medDosage • $medInstructions"
            val bigBodyNote = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "احرص على أخذ الجرعة في موعدها المعتاد للحفاظ على صحتك واستقرار حالتك."
                com.example.ui.locale.AppLanguage.RU -> "Принимайте лекарство вовремя для поддержания здоровья и стабильного состояния."
                else -> "Please take your dose on schedule to maintain your health and stability."
            }
            val summaryAppHeader = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "طبيبي الذكي • تذكير الأدوية"
                com.example.ui.locale.AppLanguage.RU -> "Смарт Медик • Напоминание о лекарствах"
                else -> "Smart Medic • Medication Reminder"
            }
            val markTakenBtnLabel = when (lang) {
                com.example.ui.locale.AppLanguage.AR -> "تم أخذ الدواء الآن"
                com.example.ui.locale.AppLanguage.RU -> "Принято сейчас"
                else -> "Mark as Taken"
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification_medication)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$contentText\n\n$bigBodyNote")
                        .setSummaryText(summaryAppHeader)
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(
                    android.R.drawable.checkbox_on_background,
                    markTakenBtnLabel,
                    markTakenPendingIntent
                )
                .build()

            val medHour = intent.getIntExtra("med_hour", -1)
            val medMinute = intent.getIntExtra("med_minute", -1)
            val medFrequency = intent.getStringExtra("med_frequency") ?: "يومياً"

            notificationManager?.notify(medId.toInt(), notification)

            // Reschedule for next day if this is a recurring medication
            val asNeededLabels = listOf("عند اللزوم", "As needed", "По необходимости")
            if (medHour in 0..23 && medMinute in 0..59 && medFrequency !in asNeededLabels) {
                MedicationReminderScheduler.scheduleNextDailyOccurrence(
                    context = context,
                    medId = medId,
                    hour = medHour,
                    minute = medMinute,
                    name = medName,
                    dosage = medDosage,
                    instructions = medInstructions,
                    patientName = patientName,
                    frequency = medFrequency
                )
            }
        } finally {
            if (wakeLock?.isHeld == true) {
                wakeLock.release()
            }
        }
    }
}
