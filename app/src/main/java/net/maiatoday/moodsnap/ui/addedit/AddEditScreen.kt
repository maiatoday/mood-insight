package net.maiatoday.moodsnap.ui.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEntrySaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isEntrySaved) {
        if (uiState.isEntrySaved) {
            onEntrySaved()
        }
    }

    AddEditContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onMoodChange = viewModel::onMoodScoreChange,
        onNotesChange = viewModel::onNotesChange,
        onMovementChange = viewModel::onMovementChange,
        onSleepChange = viewModel::onSleepChange,
        onSunlightChange = viewModel::onSunlightChange,
        onEnergyChange = viewModel::onEnergyChange,
        onSaveClick = viewModel::saveEntry,
        onTagAdd = viewModel::addTag,
        onTagRemove = viewModel::removeTag,
        onTagCreate = viewModel::createNewTag
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditContent(
    uiState: AddEditUiState,
    onBackClick: () -> Unit,
    onMoodChange: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
    onMovementChange: (Boolean) -> Unit,
    onSleepChange: (Boolean) -> Unit,
    onSunlightChange: (Boolean) -> Unit,
    onEnergyChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onTagAdd: (String) -> Unit = {},
    onTagRemove: (String) -> Unit = {},
    onTagCreate: (String) -> Unit = {}
) {
    var showTagDialog by remember { mutableStateOf(false) }

    if (showTagDialog) {
        TagSelectionDialog(
            availableTags = uiState.availableTags,
            selectedTags = uiState.tags,
            onTagSelected = {
                onTagAdd(it)
                showTagDialog = false
            },
            onTagCreated = {
                onTagCreate(it)
                showTagDialog = false
            },
            onDismissRequest = { showTagDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add/Edit Mood") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoodScoreSelector(
                moodScore = uiState.moodScore,
                onMoodChange = onMoodChange
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            HabitToggleRow(
                label = "Movement",
                checked = uiState.movement,
                onCheckedChange = onMovementChange
            )
            HabitToggleRow(
                label = "Sleep",
                checked = uiState.sleep,
                onCheckedChange = onSleepChange
            )
            HabitToggleRow(
                label = "Sunlight",
                checked = uiState.sunlight,
                onCheckedChange = onSunlightChange
            )

            EnergySlider(
                energy = uiState.energy,
                onEnergyChange = onEnergyChange
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tags", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.tags.forEach { tag ->
                        InputChip(
                            selected = true,
                            onClick = { /* Do nothing or edit? */ },
                            label = { Text(tag) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove Tag",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onTagRemove(tag) }
                                )
                            }
                        )
                    }
                    IconButton(onClick = { showTagDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Tag")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun MoodScoreSelector(
    moodScore: Int,
    onMoodChange: (Int) -> Unit
) {
    val emojis = listOf("😢", "😐", "🙂", "😄", "🤩")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Mood", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            emojis.forEachIndexed { index, emoji ->
                val score = index + 1
                val isSelected = moodScore == score
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .clickable { onMoodChange(score) }
                        .padding(8.dp)
                        .alpha(if (isSelected) 1f else 0.3f)
                )
            }
        }
    }
}

@Composable
fun HabitToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = { onCheckedChange(true) }) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Yes",
                    tint = if (checked) MaterialTheme.colorScheme.primary else Color.Gray,
                )
            }
            IconButton(onClick = { onCheckedChange(false) }) {
                Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = "No",
                    tint = if (!checked) MaterialTheme.colorScheme.error else Color.Gray,
                )
            }
        }
    }
}

@Composable
fun EnergySlider(
    energy: Int,
    onEnergyChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Energy: $energy", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = energy.toFloat(),
            onValueChange = { onEnergyChange(it.toInt()) },
            valueRange = 0f..15f,
            steps = 14
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditScreenPreview() {
    MaterialTheme {
        AddEditContent(
            uiState = AddEditUiState(
                moodScore = 4,
                tags = listOf("Work", "Coffee"),
                movement = true,
                sleep = false,
                sunlight = true,
                energy = 12
            ),
            onBackClick = {},
            onMoodChange = {},
            onNotesChange = {},
            onMovementChange = {},
            onSleepChange = {},
            onSunlightChange = {},
            onEnergyChange = {},
            onSaveClick = {}
        )
    }
}
