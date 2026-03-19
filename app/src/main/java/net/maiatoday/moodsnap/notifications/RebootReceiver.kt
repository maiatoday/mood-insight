package net.maiatoday.moodsnap.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.maiatoday.moodsnap.data.UserPreferenceRepository
import javax.inject.Inject

@AndroidEntryPoint
class RebootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userPreferenceRepository: UserPreferenceRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
            
            scope.launch {
                val prefs = userPreferenceRepository.preferencesFlow.first()
                if (prefs.reminderEnabled) {
                    reminderScheduler.scheduleReminder(prefs.reminderHour, prefs.reminderMinute)
                }
            }
        }
    }
}
