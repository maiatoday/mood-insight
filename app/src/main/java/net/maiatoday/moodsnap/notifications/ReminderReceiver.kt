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
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userPreferenceRepository: UserPreferenceRepository

    @Inject
    lateinit var notificationHelper: INotificationHelper

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        // Tell Android to keep the CPU awake because we are doing async work
        val pendingResult = goAsync()
        
        scope.launch {
            try {
                val prefs = userPreferenceRepository.preferencesFlow.first()
                if (prefs.reminderEnabled) {
                    notificationHelper.showReminderNotification()
                }
            } finally {
                // Must be called so the system knows it can sleep the device again
                pendingResult.finish()
            }
        }
    }
}
