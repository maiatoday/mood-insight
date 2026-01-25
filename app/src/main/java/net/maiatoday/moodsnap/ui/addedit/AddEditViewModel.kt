package net.maiatoday.moodsnap.ui.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.maiatoday.moodsnap.data.MoodEntry
import net.maiatoday.moodsnap.data.MoodEntryDao
import java.util.Date
import javax.inject.Inject

data class AddEditUiState(
    val moods: List<String> = emptyList(),
    val notes: String = "",
    val sport: Boolean = false,
    val sunlight: Boolean = false,
    val sleep: String = "",
    val food: String = "",
    val isEntrySaved: Boolean = false
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val moodEntryDao: MoodEntryDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var entryId: Int? = savedStateHandle.get<Int>("entryId")

    init {
        if (entryId != null) {
            viewModelScope.launch {
                moodEntryDao.getEntryById(entryId!!).collect { entry ->
                    _uiState.value = AddEditUiState(
                        moods = entry.moods,
                        notes = entry.notes,
                        sport = entry.sport,
                        sunlight = entry.sunlight,
                        sleep = entry.sleep,
                        food = entry.food
                    )
                }
            }
        }
    }

    fun onMoodsChange(moods: List<String>) {
        _uiState.value = _uiState.value.copy(moods = moods)
    }

    fun onNotesChange(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun onSportChange(sport: Boolean) {
        _uiState.value = _uiState.value.copy(sport = sport)
    }

    fun onSunlightChange(sunlight: Boolean) {
        _uiState.value = _uiState.value.copy(sunlight = sunlight)
    }

    fun onSleepChange(sleep: String) {
        _uiState.value = _uiState.value.copy(sleep = sleep)
    }

    fun onFoodChange(food: String) {
        _uiState.value = _uiState.value.copy(food = food)
    }

    fun saveEntry() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val moodEntry = MoodEntry(
                id = entryId ?: 0,
                moods = currentState.moods,
                notes = currentState.notes,
                sport = currentState.sport,
                sunlight = currentState.sunlight,
                sleep = currentState.sleep,
                food = currentState.food,
                timestamp = Date()
            )
            moodEntryDao.insert(moodEntry)
            _uiState.value = _uiState.value.copy(isEntrySaved = true)
        }
    }
}
