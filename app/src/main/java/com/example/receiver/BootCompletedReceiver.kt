package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.MedicalDatabase
import com.example.util.MedicationReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * يستقبل إعادة تشغيل الجهاز (Boot Completed) أو تحديث التطبيق لإعادة جدولة كافة تنبيهات الأدوية
 * تلقائياً في الخلفية عبر AlarmManager دون الحاجة لفتح التطبيق من قبل المستخدم.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("BootReceiver", "Device reboot detected: $action. Rescheduling medication alarms...")
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = MedicalDatabase.getDatabase(context)
                    val userProfile = db.medicalDao().getUserProfileOnce()
                    val patientName = userProfile?.name ?: ""
                    val medications = db.medicalDao().getAllMedicationsList()

                    val activeMeds = medications.filter { it.isActive }
                    for (med in activeMeds) {
                        MedicationReminderScheduler.scheduleReminder(
                            context = context,
                            med = med,
                            patientName = patientName
                        )
                    }
                    Log.d("BootReceiver", "Successfully rescheduled ${activeMeds.size} active medication alarms.")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling medication alarms after boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
