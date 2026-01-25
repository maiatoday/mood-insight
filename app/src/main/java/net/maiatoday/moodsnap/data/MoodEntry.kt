package net.maiatoday.moodsnap.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val moods: List<String>,
    val notes: String,
    val sport: Boolean,
    val sunlight: Boolean,
    val sleep: String,
    val food: String,
    val timestamp: Date = Date()
)
