package net.maiatoday.moodsnap.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.maiatoday.moodsnap.data.MoodEntry

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddEntry: () -> Unit,
    onEntryClick: (Int) -> Unit
) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(entries) { entry ->
                MoodEntryItem(entry = entry, onClick = { onEntryClick(entry.id) })
            }
        }
    }
}

@Composable
fun MoodEntryItem(entry: MoodEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Moods: ${entry.moods.joinToString()}")
            Text("Notes: ${entry.notes}")
        }
    }
}
