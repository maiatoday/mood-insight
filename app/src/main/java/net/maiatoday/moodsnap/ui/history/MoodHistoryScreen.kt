package net.maiatoday.moodsnap.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import net.maiatoday.moodsnap.data.MoodEntry
import net.maiatoday.moodsnap.data.MoodEntryWithTags
import java.util.Date

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
        items(entries) { entryWithTags ->
            MoodEntryItem(entryWithTags = entryWithTags, onClick = { onEntryClick(entryWithTags.moodEntry.id) })
        }
    }
}

@Composable
fun MoodEntryItem(entryWithTags: MoodEntryWithTags, onClick: () -> Unit) {
    val entry = entryWithTags.moodEntry
    val tags = entryWithTags.tags.map { it.name }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Score: ${entry.moodScore}")
            Text("Tags: ${tags.joinToString()}")
            Text("Notes: ${entry.notes}")
            Text("Energy: ${entry.energy}")
        }
    }
}

@Preview
@Composable
fun MoodHistoryScreenPreview() {
    val sampleEntry = MoodEntry(
        id = 1,
        moodScore = 4,
        timestamp = Date(),
        notes = "Great day!",
        energy = 5,
        movement = true,
        sunlight = true,
        sleep = true
    )
    val sampleEntryWithTags = MoodEntryWithTags(
        moodEntry = sampleEntry,
        tags = emptyList()
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(listOf(sampleEntryWithTags)) { entry ->
            MoodEntryItem(entryWithTags = entry, onClick = {})
        }
    }
}
