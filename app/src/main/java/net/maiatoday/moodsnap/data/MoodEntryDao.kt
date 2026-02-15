package net.maiatoday.moodsnap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    fun getEntryById(id: Int): Flow<MoodEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry)

    @Update
    suspend fun update(entry: MoodEntry)

    @Delete
    suspend fun delete(entry: MoodEntry)
}
