package net.maiatoday.moodsnap.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.maiatoday.moodsnap.domain.DailyMood
import net.maiatoday.moodsnap.domain.Mood
import net.maiatoday.moodsnap.domain.WeeklySummary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddEntry: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Snap") },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "History"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (summary != null) {
                HomeContent(summary!!)
            } else {
                // Loading or Empty State
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading...")
                }
            }
        }
    }
}

@Composable
fun HomeContent(summary: WeeklySummary) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mood Graph and Current Status
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Graph
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    ) {
                        Text("Mood History", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        MoodGraph(
                            dailyMoods = summary.dailyMoods,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Current Status
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val currentMood = summary.currentMood ?: Mood.OK
                        Text("Today", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentMood.emoji,
                            style = MaterialTheme.typography.displayLarge,
                            color = currentMood.color
                        )
                    }
                }
            }
        }

        // Averages
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Past week", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", summary.averageMood),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Resonance", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", summary.resonance),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Energy", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", summary.averageEnergy),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Tags
        item {
            Text("Recent Tags", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(summary.tags) { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag) }
                    )
                }
            }
        }

        // Habit Tracker
        item {
            Text("Habits (Past 7 Days)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HabitRow("Movement", summary.movementCount)
                    HabitRow("Sunlight", summary.sunlightCount)
                    HabitRow("Sleep", summary.sleepCount)
                }
            }
        }

        // Spacer for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MoodGraph(dailyMoods: List<DailyMood>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        if (dailyMoods.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Data", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            dailyMoods.forEach { mood ->
                val moodEnum = mood.mood
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .fillMaxHeight(moodEnum.score / 5f)
                                .background(
                                    moodEnum.color,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = mood.dayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun HabitRow(label: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.width(100.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Row {
            repeat(7) { index ->
                Icon(
                    imageVector = if (index < count) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (index < count) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummySummary = WeeklySummary(
        averageMood = 4.2f,
        averageEnergy = 3.5f,
        resonance = 4.5f,
        movementCount = 4,
        sunlightCount = 5,
        sleepCount = 6,
        tags = listOf("Work", "Exercise", "Reading", "Junk Food"),
        dailyMoods = listOf(
            DailyMood("Mon", Mood.OK),
            DailyMood("Tue", Mood.GOOD),
            DailyMood("Wed", Mood.GREAT),
            DailyMood("Thu", Mood.MEH),
            DailyMood("Fri", Mood.GOOD),
            DailyMood("Sat", Mood.GREAT),
            DailyMood("Sun", Mood.GOOD)
        ),
        currentMood = Mood.GOOD
    )

    MaterialTheme {
        HomeContent(summary = dummySummary)
    }
}
