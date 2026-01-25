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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

val allMoods = listOf("Happy", "Sad", "Anxious", "Excited", "Calm", "Angry")

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
            MoodSelector(
                moods = uiState.moods,
                onMoodsChange = viewModel::onMoodsChange
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
                    checked = uiState.sport,
                    onCheckedChange = viewModel::onSportChange
                )
                Text("Sport")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.sunlight,
                    onCheckedChange = viewModel::onSunlightChange
                )
                Text("Sunlight")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.sleep,
                onValueChange = viewModel::onSleepChange,
                label = { Text("Sleep") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.food,
                onValueChange = viewModel::onFoodChange,
                label = { Text("Food") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = viewModel::saveEntry) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodSelector(
    moods: List<String>,
    onMoodsChange: (List<String>) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        allMoods.forEach { mood ->
            val isSelected = moods.contains(mood)
            ElevatedButton(
                onClick = {
                    val newMoods = if (isSelected) {
                        moods - mood
                    } else {
                        moods + mood
                    }
                    onMoodsChange(newMoods)
                },
            ) {
                Text(mood)
            }
        }
    }
}