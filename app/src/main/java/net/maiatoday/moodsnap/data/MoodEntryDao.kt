package net.maiatoday.moodsnap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    fun getEntryById(id: Int): Flow<MoodEntry?>

    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    fun getEntriesFromDate(startDate: Date): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: MoodEntry): Long

    @Update
    suspend fun update(entry: MoodEntry)

    @Delete
    suspend fun delete(entry: MoodEntry)

    @Transaction
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getEntriesWithTags(): Flow<List<MoodEntryWithTags>>
    
    @Transaction
    @Query("SELECT * FROM mood_entries WHERE id = :id")
    fun getEntryWithTagsById(id: Int): Flow<MoodEntryWithTags?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoodEntryTagCrossRef(crossRef: MoodEntryTagCrossRef)

    @Delete
    suspend fun deleteMoodEntryTagCrossRef(crossRef: MoodEntryTagCrossRef)
    
    @Query("DELETE FROM mood_entry_tag_cross_ref WHERE moodEntryId = :moodEntryId")
    suspend fun deleteTagsForEntry(moodEntryId: Int)
}
