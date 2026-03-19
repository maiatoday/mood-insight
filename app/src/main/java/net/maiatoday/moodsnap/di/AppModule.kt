package net.maiatoday.moodsnap.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.maiatoday.moodsnap.data.AppDatabase
import net.maiatoday.moodsnap.data.UserPreferenceRepository
import net.maiatoday.moodsnap.data.MoodEntryDao
import net.maiatoday.moodsnap.data.TagDao
import net.maiatoday.moodsnap.data.DefaultUserPreferenceRepository
import net.maiatoday.moodsnap.notifications.INotificationHelper
import net.maiatoday.moodsnap.notifications.NotificationHelper
import net.maiatoday.moodsnap.notifications.IReminderScheduler
import net.maiatoday.moodsnap.notifications.ReminderScheduler
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    internal val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Create the new table without the 'tags' column
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `mood_entries_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `moodScore` INTEGER NOT NULL, 
                    `notes` TEXT NOT NULL, 
                    `movement` INTEGER NOT NULL, 
                    `sunlight` INTEGER NOT NULL, 
                    `sleep` INTEGER NOT NULL, 
                    `energy` INTEGER NOT NULL, 
                    `timestamp` INTEGER NOT NULL
                )
            """)

            // 2. Copy data from the old table to the new table
            db.execSQL("""
                INSERT INTO mood_entries_new (id, moodScore, notes, movement, sunlight, sleep, energy, timestamp)
                SELECT id, moodScore, notes, movement, sunlight, sleep, energy, timestamp FROM mood_entries
            """)

            // 3. Drop the old table
            db.execSQL("DROP TABLE mood_entries")

            // 4. Rename the new table to the original name
            db.execSQL("ALTER TABLE mood_entries_new RENAME TO mood_entries")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "mood_snap_database"
        )
        .addMigrations(MIGRATION_2_3)
        .fallbackToDestructiveMigration(false)
        .build()
    }

    @Provides
    fun provideMoodEntryDao(database: AppDatabase): MoodEntryDao {
        return database.moodEntryDao()
    }

    @Provides
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferenceRepository(dataStore: DataStore<Preferences>): UserPreferenceRepository {
        return DefaultUserPreferenceRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(@ApplicationContext context: Context): IReminderScheduler {
        return ReminderScheduler(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): INotificationHelper {
        return NotificationHelper(context)
    }
}
