package net.maiatoday.moodsnap.domain

import net.maiatoday.moodsnap.data.UserPreferenceRepository
import net.maiatoday.moodsnap.notifications.IReminderScheduler
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val reminderScheduler: IReminderScheduler
) {
    suspend operator fun invoke(enabled: Boolean, hour: Int, minute: Int) {
        userPreferenceRepository.updateReminderEnabled(enabled)
        userPreferenceRepository.updateReminderTime(hour, minute)

        if (enabled) {
            reminderScheduler.scheduleReminder(hour, minute)
        } else {
            reminderScheduler.cancelReminder()
        }
    }
}
