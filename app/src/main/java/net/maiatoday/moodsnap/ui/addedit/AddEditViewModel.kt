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
import net.maiatoday.moodsnap.data.MoodRepository
import net.maiatoday.moodsnap.data.Tag
import java.time.Instant
import java.util.Date
import javax.inject.Inject

data class AddEditUiState(
    val moodScore: Int = 0,
    val tags: List<String> = emptyList(), // Selected tags for this entry
    val availableTags: List<String> = emptyList(), // All available tags from DB
    val notes: String = "",
    val movement: Boolean = false,
    val sunlight: Boolean = false,
    val sleep: Boolean = false,
    val energy: Int = 0,
    val timestamp: Instant = Instant.now(),
    val isEntrySaved: Boolean = false
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var isNew: Boolean = true
    private var moodId: Int = 0

    init {
        // Load all available tags
        viewModelScope.launch {
            moodRepository.getAllTags().collect { tags ->
                _uiState.value = _uiState.value.copy(availableTags = tags.map { it.name })
            }
        }

        val entryId = savedStateHandle.get<Int>("entryId")
        if (entryId != null && entryId != -1) {
            isNew = false
            moodId = entryId
            viewModelScope.launch {
                moodRepository.getEntryWithTagsById(entryId).collect { entryWithTags ->
                    if (entryWithTags != null) {
                        _uiState.value = _uiState.value.copy(
                            moodScore = entryWithTags.moodEntry.moodScore,
                            tags = entryWithTags.tags.map { it.name },
                            notes = entryWithTags.moodEntry.notes,
                            movement = entryWithTags.moodEntry.movement,
                            sunlight = entryWithTags.moodEntry.sunlight,
                            sleep = entryWithTags.moodEntry.sleep,
                            energy = entryWithTags.moodEntry.energy,
                            timestamp = entryWithTags.moodEntry.timestamp.toInstant()
                        )
                    }
                }
            }
        }
    }

    fun onMoodScoreChange(moodScore: Int) {
        _uiState.value = _uiState.value.copy(moodScore = moodScore)
    }

    fun addTag(tagName: String) {
        val currentTags = _uiState.value.tags.toMutableList()
        if (!currentTags.contains(tagName)) {
            currentTags.add(tagName)
            _uiState.value = _uiState.value.copy(tags = currentTags)
        }
    }

    fun removeTag(tagName: String) {
        val currentTags = _uiState.value.tags.toMutableList()
        currentTags.remove(tagName)
        _uiState.value = _uiState.value.copy(tags = currentTags)
    }

    fun createNewTag(tagName: String) {
        viewModelScope.launch {
            moodRepository.insertTag(Tag(tagName))
            addTag(tagName)
        }
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
                notes = currentState.notes,
                movement = currentState.movement,
                sunlight = currentState.sunlight,
                sleep = currentState.sleep,
                energy = currentState.energy,
                timestamp = Date.from(currentState.timestamp)
            )

            val savedId = if (isNew) {
                moodRepository.insert(moodEntry)
            } else {
                moodRepository.update(moodEntry)
                moodId.toLong()
            }

            moodRepository.updateTagsForEntry(savedId.toInt(), currentState.tags)

            _uiState.value = _uiState.value.copy(isEntrySaved = true)
        }
    }
}
