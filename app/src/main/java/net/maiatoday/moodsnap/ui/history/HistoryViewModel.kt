package net.maiatoday.moodsnap.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import net.maiatoday.moodsnap.domain.GetMoodEntriesUseCase
import net.maiatoday.moodsnap.domain.MoodEntryDomain
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getMoodEntriesUseCase: GetMoodEntriesUseCase
) : ViewModel() {

    val entries: StateFlow<List<MoodEntryDomain>> = getMoodEntriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}