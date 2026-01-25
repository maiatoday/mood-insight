package net.maiatoday.moodsnap.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.maiatoday.moodsnap.data.AppDatabase
import net.maiatoday.moodsnap.data.MoodEntryDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "mood_snap_database"
        ).build()
    }

    @Provides
    fun provideMoodEntryDao(database: AppDatabase): MoodEntryDao {
        return database.moodEntryDao()
    }
}
