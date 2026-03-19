package net.maiatoday.moodsnap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.maiatoday.moodsnap.data.MoodRepository
import net.maiatoday.moodsnap.data.UserPreferences
import net.maiatoday.moodsnap.data.UserPreferenceRepository
import net.maiatoday.moodsnap.domain.ScheduleReminderUseCase
import net.maiatoday.moodsnap.notifications.INotificationHelper
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val notificationHelper: INotificationHelper
) : ViewModel() {

    private val _userPreferences = MutableStateFlow(UserPreferences(false, 20, 0))
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferenceRepository.preferencesFlow.collect { prefs ->
                _userPreferences.value = prefs
            }
        }
    }

    fun toggleReminder(enabled: Boolean, hour: Int = _userPreferences.value.reminderHour, minute: Int = _userPreferences.value.reminderMinute) {
        viewModelScope.launch {
            scheduleReminderUseCase(enabled, hour, minute)
        }
    }
    
    fun testNotification() {
        notificationHelper.showReminderNotification()
    }

    fun generateSampleData() {
        viewModelScope.launch {
            moodRepository.generateSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            moodRepository.clearAllData()
        }
    }
}
