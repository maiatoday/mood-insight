package net.maiatoday.moodsnap.ui.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import net.maiatoday.moodsnap.domain.Mood
import net.maiatoday.moodsnap.domain.MoodEntryDomain
import java.time.Instant

@Composable
fun MoodHistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onEntryClick: (Int) -> Unit
) {
    val entries by viewModel.entries.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(entries) { entryDomain ->
            MoodEntryItem(entryDomain = entryDomain, onClick = { onEntryClick(entryDomain.id) })
        }
    }
}

@Preview
@Composable
fun MoodHistoryScreenPreview() {
    val sampleEntryDomain = MoodEntryDomain(
        id = 1,
        mood = Mood.GOOD,
        notes = "Great day!",
        movement = true,
        sunlight = true,
        sleep = true,
        energy = 5,
        timestamp = Instant.now(),
        tags = listOf("Happy", "Work")
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(listOf(sampleEntryDomain)) { entry ->
            MoodEntryItem(entryDomain = entry, onClick = {})
        }
    }
}
