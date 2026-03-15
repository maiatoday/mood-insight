package net.maiatoday.moodsnap.domain

import net.maiatoday.moodsnap.data.MoodEntryWithTags

fun MoodEntryWithTags.toDomain(): MoodEntryDomain {
    return MoodEntryDomain(
        id = moodEntry.id,
        mood = Mood.fromScore(moodEntry.moodScore),
        notes = moodEntry.notes,
        movement = moodEntry.movement,
        sunlight = moodEntry.sunlight,
        sleep = moodEntry.sleep,
        energy = moodEntry.energy,
        timestamp = moodEntry.timestamp.toInstant(),
        tags = tags.map { it.name }
    )
}