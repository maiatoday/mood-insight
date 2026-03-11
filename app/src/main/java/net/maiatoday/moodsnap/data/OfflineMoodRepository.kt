package net.maiatoday.moodsnap.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineMoodRepository @Inject constructor(
    private val moodEntryDao: MoodEntryDao
) : MoodRepository {
    override fun getAllEntries(): Flow<List<MoodEntry>> = moodEntryDao.getAllEntries()

    override fun getEntryById(id: Int): Flow<MoodEntry?> = moodEntryDao.getEntryById(id)

    override suspend fun insert(entry: MoodEntry): Long = moodEntryDao.insert(entry)

    override suspend fun update(entry: MoodEntry) = moodEntryDao.update(entry)

    override suspend fun delete(entry: MoodEntry) = moodEntryDao.delete(entry)
}
