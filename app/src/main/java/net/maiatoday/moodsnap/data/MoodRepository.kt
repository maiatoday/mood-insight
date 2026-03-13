package net.maiatoday.moodsnap.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface MoodRepository {
    fun getAllEntries(): Flow<List<MoodEntry>>
    fun getEntryById(id: Int): Flow<MoodEntry?>
    fun getEntriesFromDate(startDate: Date): Flow<List<MoodEntry>>
    suspend fun insert(entry: MoodEntry): Long
    suspend fun update(entry: MoodEntry)
    suspend fun delete(entry: MoodEntry)

    // Tag related methods
    fun getAllTags(): Flow<List<Tag>>
    fun getAllEntriesWithTags(): Flow<List<MoodEntryWithTags>>
    fun getEntryWithTagsById(id: Int): Flow<MoodEntryWithTags?>
    fun getEntriesWithTagsFromDate(startDate: Date): Flow<List<MoodEntryWithTags>>
    suspend fun insertTag(tag: Tag)
    suspend fun addTagToEntry(entryId: Int, tagName: String)
    suspend fun removeTagFromEntry(entryId: Int, tagName: String)
    suspend fun updateTagsForEntry(entryId: Int, tags: List<String>)
    suspend fun generateSampleData()
    suspend fun clearAllData()
}
