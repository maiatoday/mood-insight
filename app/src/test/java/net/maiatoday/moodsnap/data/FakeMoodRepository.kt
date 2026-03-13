package net.maiatoday.moodsnap.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Date

class FakeMoodRepository : MoodRepository {
    suspend fun generateTestData(days: Int = 14) {
        val sampleTags = listOf("Work", "Home", "Gym", "Outdoor", "Relax", "Stress")
        sampleTags.forEach { insertTag(Tag(it)) }

        val now = System.currentTimeMillis()
        for (i in days downTo 0) {
            val timestamp = Date(now - (i.toLong() * 24 * 60 * 60 * 1000))

            val moodEntry = MoodEntry(
                moodScore = (i % 5) + 1,
                notes = "Sample note for day ${days - i}",
                movement = i % 2 == 0,
                sunlight = i % 3 == 0,
                sleep = i % 4 == 0,
                energy = ((i + 2) % 5) + 1,
                timestamp = timestamp
            )
            val entryId = insert(moodEntry).toInt()

            // Deterministically assign 1-3 tags
            val tagCount = (i % 3) + 1
            for (j in 0 until tagCount) {
                val tagIndex = (i + j) % sampleTags.size
                addTagToEntry(entryId, sampleTags[tagIndex])
            }
        }
    }

    private val entries = MutableStateFlow<List<MoodEntry>>(emptyList())
    private val tags = MutableStateFlow<List<Tag>>(emptyList())
    private val entryTags = MutableStateFlow<Map<Int, List<String>>>(emptyMap())

    fun entriesList() = entries.value

    override fun getAllEntries(): Flow<List<MoodEntry>> = entries

    override fun getEntryById(id: Int): Flow<MoodEntry?> = entries.map { list ->
        list.find { it.id == id }
    }

    override fun getEntriesFromDate(startDate: Date): Flow<List<MoodEntry>> = entries.map { list ->
        list.filter { it.timestamp >= startDate }
    }

    override suspend fun insert(entry: MoodEntry): Long {
        val newId = (entries.value.maxOfOrNull { it.id } ?: 0) + 1
        val newEntry = entry.copy(id = newId)
        entries.update { it + newEntry }
        return newId.toLong()
    }

    override suspend fun update(entry: MoodEntry) {
        entries.update { list ->
            list.map { if (it.id == entry.id) entry else it }
        }
    }

    override suspend fun delete(entry: MoodEntry) {
        entries.update { list ->
            list.filter { it.id != entry.id }
        }
        entryTags.update { map ->
            map.filterKeys { it != entry.id }
        }
    }

    override fun getAllTags(): Flow<List<Tag>> = tags

    override fun getAllEntriesWithTags(): Flow<List<MoodEntryWithTags>> = 
        combine(entries, tags, entryTags) { moodEntries, tagList, tagMap ->
            moodEntries.map { entry ->
                val tagNames = tagMap[entry.id] ?: emptyList()
                val entryTagsList = tagList.filter { tagNames.contains(it.name) }
                MoodEntryWithTags(entry, entryTagsList)
            }
        }

    override fun getEntryWithTagsById(id: Int): Flow<MoodEntryWithTags?> = 
        combine(entries, tags, entryTags) { moodEntries, tagList, tagMap ->
            val entry = moodEntries.find { it.id == id }
            entry?.let { moodEntry ->
                val tagNames = tagMap[moodEntry.id] ?: emptyList()
                val entryTagsList = tagList.filter { tagNames.contains(it.name) }
                MoodEntryWithTags(moodEntry, entryTagsList)
            }
        }

    override fun getEntriesWithTagsFromDate(startDate: Date): Flow<List<MoodEntryWithTags>> = 
        combine(entries, tags, entryTags) { moodEntries, tagList, tagMap ->
            moodEntries.filter { it.timestamp >= startDate }.map { entry ->
                val tagNames = tagMap[entry.id] ?: emptyList()
                val entryTagsList = tagList.filter { tagNames.contains(it.name) }
                MoodEntryWithTags(entry, entryTagsList)
            }
        }

    override suspend fun insertTag(tag: Tag) {
        if (tags.value.none { it.name == tag.name }) {
            tags.update { it + tag }
        }
    }

    override suspend fun addTagToEntry(entryId: Int, tagName: String) {
        entryTags.update { map ->
            val currentTags = map[entryId] ?: emptyList()
            if (!currentTags.contains(tagName)) {
                map + (entryId to (currentTags + tagName))
            } else {
                map
            }
        }
    }

    override suspend fun removeTagFromEntry(entryId: Int, tagName: String) {
        entryTags.update { map ->
            val currentTags = map[entryId] ?: emptyList()
            map + (entryId to (currentTags - tagName))
        }
    }

    override suspend fun updateTagsForEntry(entryId: Int, tags: List<String>) {
        entryTags.update { map ->
            map + (entryId to tags)
        }
    }

    override suspend fun generateSampleData() {
        generateTestData(21)
    }

    override suspend fun clearAllData() {
        entries.value = emptyList()
        tags.value = emptyList()
        entryTags.value = emptyMap()
    }
}
