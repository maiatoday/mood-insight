package net.maiatoday.moodsnap.ui.addedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

val allTags = listOf("Work", "Family", "Friends", "Hobby", "Travel", "Health")

@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel = hiltViewModel(),
    onEntrySaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isEntrySaved) {
        onEntrySaved()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            MoodScoreSelector(
                moodScore = uiState.moodScore,
                onMoodScoreChange = viewModel::onMoodScoreChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            TagSelector(
                tags = uiState.tags,
                onTagsChange = viewModel::onTagsChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.movement,
                    onCheckedChange = viewModel::onMovementChange
                )
                Text("Movement")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.sleep,
                    onCheckedChange = viewModel::onSleepChange
                )
                Text("Sleep")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.sunlight,
                    onCheckedChange = viewModel::onSunlightChange
                )
                Text("Sunlight")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Energy: ${uiState.energy}")
            Slider(
                value = uiState.energy.toFloat(),
                onValueChange = { viewModel.onEnergyChange(it.toInt()) },
                valueRange = 0f..15f,
                steps = 14
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = viewModel::saveEntry) {
                Text("Save")
            }
        }
    }
}

@Composable
fun MoodScoreSelector(
    moodScore: Int,
    onMoodScoreChange: (Int) -> Unit
) {
    val moodDescriptions = mapOf(
        -2 to "Very Bad",
        -1 to "Bad",
        0 to "Neutral",
        1 to "Good",
        2 to "Very Good"
    )
    Column {
        Text("Mood Score: ${moodDescriptions[moodScore]}")
        Slider(
            value = moodScore.toFloat(),
            onValueChange = { onMoodScoreChange(it.toInt()) },
            valueRange = -2f..2f,
            steps = 3
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        allTags.forEach { tag ->
            val isSelected = tags.contains(tag)
            ElevatedButton(
                onClick = {
                    val newTags = if (isSelected) {
                        tags - tag
                    } else {
                        tags + tag
                    }
                    onTagsChange(newTags)
                },
            ) {
                Text(tag)
            }
        }
    }
}
