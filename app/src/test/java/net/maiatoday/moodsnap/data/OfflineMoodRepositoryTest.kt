package net.maiatoday.moodsnap.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class OfflineMoodRepositoryTest {

    private lateinit var repository: OfflineMoodRepository
    private lateinit var fakeMoodEntryDao: FakeMoodEntryDao
    private lateinit var fakeTagDao: FakeTagDao

    @Before
    fun setup() {
        fakeMoodEntryDao = FakeMoodEntryDao()
        fakeTagDao = FakeTagDao()
        repository = OfflineMoodRepository(fakeMoodEntryDao, fakeTagDao)
    }

    @Test
    fun `updateTagsForEntry clears existing tags and adds new ones`() = runTest {
        // Given an entry with an existing tag
        val entryId = 1
        fakeMoodEntryDao.insertMoodEntryTagCrossRef(MoodEntryTagCrossRef(entryId, "OldTag"))

        // When updating tags
        val newTags = listOf("Happy", "Productive")
        repository.updateTagsForEntry(entryId, newTags)

        // Then old tags should be gone and only new tags should remain
        val currentTags = fakeMoodEntryDao.crossRefs.value[entryId] ?: emptyList()
        assertEquals(2, currentTags.size)
        assertEquals(newTags, currentTags)
    }

    private class FakeMoodEntryDao : MoodEntryDao {
        val crossRefs = MutableStateFlow<Map<Int, List<String>>>(emptyMap())

        override suspend fun insertMoodEntryTagCrossRef(crossRef: MoodEntryTagCrossRef) {
            val current = crossRefs.value[crossRef.moodEntryId] ?: emptyList()
            crossRefs.value += (crossRef.moodEntryId to (current + crossRef.tagName))
        }

        override suspend fun deleteTagsForEntry(moodEntryId: Int) {
            crossRefs.value += (moodEntryId to emptyList())
        }

        override fun getAllEntries(): Flow<List<MoodEntry>> = TODO()
        override fun getEntryById(id: Int): Flow<MoodEntry?> = TODO()
        override fun getEntriesFromDate(startDate: Date): Flow<List<MoodEntry>> = TODO()
        override fun getEntriesWithTagsFromDate(startDate: Date): Flow<List<MoodEntryWithTags>> = TODO()
        override suspend fun insert(entry: MoodEntry): Long = 0
        override suspend fun update(entry: MoodEntry) {}
        override suspend fun delete(entry: MoodEntry) {}
        override fun getEntriesWithTags(): Flow<List<MoodEntryWithTags>> = TODO()
        override fun getEntryWithTagsById(id: Int): Flow<MoodEntryWithTags?> = TODO()
        override suspend fun deleteMoodEntryTagCrossRef(crossRef: MoodEntryTagCrossRef) {}
        override suspend fun deleteAllEntries() {}
    }

    private class FakeTagDao : TagDao {
        override fun getAllTags(): Flow<List<Tag>> = TODO()
        override suspend fun insert(tag: Tag) {}
        override suspend fun delete(tag: Tag) {}
        override suspend fun deleteAllTags() {}
    }
}
