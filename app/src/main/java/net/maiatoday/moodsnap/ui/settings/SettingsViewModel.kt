package net.maiatoday.moodsnap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.maiatoday.moodsnap.data.MoodRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : ViewModel() {

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
