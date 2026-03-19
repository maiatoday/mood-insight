package net.maiatoday.moodsnap.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeUserPreferenceRepository : UserPreferenceRepository {

    private val _preferencesFlow = MutableStateFlow(UserPreferences(false, 20, 0))
    override val preferencesFlow: Flow<UserPreferences> = _preferencesFlow.asStateFlow()

    override suspend fun updateReminderEnabled(enabled: Boolean) {
        _preferencesFlow.update { it.copy(reminderEnabled = enabled) }
    }

    override suspend fun updateReminderTime(hour: Int, minute: Int) {
        _preferencesFlow.update { it.copy(reminderHour = hour, reminderMinute = minute) }
    }
}
