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
    val moodScore: Int = 0,
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val movement: Boolean = false,
    val sunlight: Boolean = false,
    val sleep: Boolean = false,
    val energy: Int = 0,
    val timestamp: Date = Date(),
    val isEntrySaved: Boolean = false
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val moodEntryDao: MoodEntryDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var isNew: Boolean = true
    private var moodId: Int = 0

    init {
        val entryId = savedStateHandle.get<Int>("entryId")
        if (entryId != null && entryId != -1) {
            isNew = false
            moodId = entryId
            viewModelScope.launch {
                moodEntryDao.getEntryById(entryId).collect { entry ->
                    if (entry != null) {
                        _uiState.value = AddEditUiState(
                            moodScore = entry.moodScore,
                            tags = entry.tags,
                            notes = entry.notes,
                            movement = entry.movement,
                            sunlight = entry.sunlight,
                            sleep = entry.sleep,
                            energy = entry.energy,
                            timestamp = entry.timestamp
                        )
                    }
                }
            }
        }
    }

    fun onMoodScoreChange(moodScore: Int) {
        _uiState.value = _uiState.value.copy(moodScore = moodScore)
    }

    fun onTagsChange(tags: List<String>) {
        _uiState.value = _uiState.value.copy(tags = tags)
    }

    fun onNotesChange(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun onMovementChange(movement: Boolean) {
        _uiState.value = _uiState.value.copy(movement = movement)
    }

    fun onSunlightChange(sunlight: Boolean) {
        _uiState.value = _uiState.value.copy(sunlight = sunlight)
    }
    
    fun onSleepChange(sleep: Boolean) {
        _uiState.value = _uiState.value.copy(sleep = sleep)
    }

    fun onEnergyChange(energy: Int) {
        _uiState.value = _uiState.value.copy(energy = energy)
    }

    fun saveEntry() {
        viewModelScope.launch {
            val currentState = _uiState.value

            val moodEntry = MoodEntry(
                id = moodId,
                moodScore = currentState.moodScore,
                tags = currentState.tags,
                notes = currentState.notes,
                movement = currentState.movement,
                sunlight = currentState.sunlight,
                sleep = currentState.sleep,
                energy = currentState.energy,
                timestamp = currentState.timestamp
            )

            if (isNew) {
                moodEntryDao.insert(moodEntry)
            } else {
                moodEntryDao.update(moodEntry)
            }

            _uiState.value = _uiState.value.copy(isEntrySaved = true)
        }
    }
}
