package net.maiatoday.moodsnap.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import net.maiatoday.moodsnap.data.MoodEntry
import net.maiatoday.moodsnap.data.MoodEntryDao
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    moodEntryDao: MoodEntryDao
) : ViewModel() {

    val entries: StateFlow<List<MoodEntry>> = moodEntryDao.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
