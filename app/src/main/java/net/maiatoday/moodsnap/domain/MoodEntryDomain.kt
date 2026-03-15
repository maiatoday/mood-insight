package net.maiatoday.moodsnap.domain

import java.time.Instant

data class MoodEntryDomain(
    val id: Int,
    val mood: Mood,
    val notes: String,
    val movement: Boolean,
    val sunlight: Boolean,
    val sleep: Boolean,
    val energy: Int,
    val timestamp: Instant,
    val tags: List<String>
)