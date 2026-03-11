package net.maiatoday.moodsnap.data

import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    fun getAllEntries(): Flow<List<MoodEntry>>
    fun getEntryById(id: Int): Flow<MoodEntry?>
    suspend fun insert(entry: MoodEntry): Long
    suspend fun update(entry: MoodEntry)
    suspend fun delete(entry: MoodEntry)
}
