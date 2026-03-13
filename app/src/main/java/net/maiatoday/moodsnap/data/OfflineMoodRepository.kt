package net.maiatoday.moodsnap.data

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import java.util.Random
import javax.inject.Inject

class OfflineMoodRepository @Inject constructor(
    private val moodEntryDao: MoodEntryDao,
    private val tagDao: TagDao
) : MoodRepository {
    override fun getAllEntries(): Flow<List<MoodEntry>> = moodEntryDao.getAllEntries()

    override fun getEntryById(id: Int): Flow<MoodEntry?> = moodEntryDao.getEntryById(id)

    override fun getEntriesFromDate(startDate: Date): Flow<List<MoodEntry>> = moodEntryDao.getEntriesFromDate(startDate)

    override suspend fun insert(entry: MoodEntry): Long = moodEntryDao.insert(entry)

    override suspend fun update(entry: MoodEntry) = moodEntryDao.update(entry)

    override suspend fun delete(entry: MoodEntry) = moodEntryDao.delete(entry)

    // Tag related methods
    override fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    override fun getAllEntriesWithTags(): Flow<List<MoodEntryWithTags>> = moodEntryDao.getEntriesWithTags()

    override fun getEntryWithTagsById(id: Int): Flow<MoodEntryWithTags?> = moodEntryDao.getEntryWithTagsById(id)

    override fun getEntriesWithTagsFromDate(startDate: Date): Flow<List<MoodEntryWithTags>> = moodEntryDao.getEntriesWithTagsFromDate(startDate)

    override suspend fun insertTag(tag: Tag) = tagDao.insert(tag)

    override suspend fun addTagToEntry(entryId: Int, tagName: String) {
        val crossRef = MoodEntryTagCrossRef(moodEntryId = entryId, tagName = tagName)
        moodEntryDao.insertMoodEntryTagCrossRef(crossRef)
    }

    override suspend fun removeTagFromEntry(entryId: Int, tagName: String) {
        val crossRef = MoodEntryTagCrossRef(moodEntryId = entryId, tagName = tagName)
        moodEntryDao.deleteMoodEntryTagCrossRef(crossRef)
    }

    @Transaction
    override suspend fun updateTagsForEntry(entryId: Int, tags: List<String>) {
        // Clear existing tags for this entry
        moodEntryDao.deleteTagsForEntry(entryId)
        // Add new tags
        tags.forEach { tagName ->
            val crossRef = MoodEntryTagCrossRef(moodEntryId = entryId, tagName = tagName)
            moodEntryDao.insertMoodEntryTagCrossRef(crossRef)
        }
    }

    override suspend fun generateSampleData() {
        val calendar = Calendar.getInstance()
        val random = Random()
        for (i in 0 until 21) {
            val entry = MoodEntry(
                moodScore = random.nextInt(5) + 1,
                notes = "Sample data day $i",
                movement = random.nextBoolean(),
                sunlight = random.nextBoolean(),
                sleep = random.nextBoolean(),
                energy = random.nextInt(5) + 1,
                timestamp = calendar.time
            )
            val entryId = moodEntryDao.insert(entry).toInt()
            
            // Add some tags
            val tagNames = listOf("Work", "Social", "Exercise", "Reading", "Hobby", "Relax")
            val selectedTags = tagNames.shuffled().take(random.nextInt(3) + 1)
            selectedTags.forEach { tagName ->
                tagDao.insert(Tag(tagName))
                val crossRef = MoodEntryTagCrossRef(moodEntryId = entryId, tagName = tagName)
                moodEntryDao.insertMoodEntryTagCrossRef(crossRef)
            }
            
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
    }

    override suspend fun clearAllData() {
        moodEntryDao.deleteAllEntries()
        tagDao.deleteAllTags()
    }
}
