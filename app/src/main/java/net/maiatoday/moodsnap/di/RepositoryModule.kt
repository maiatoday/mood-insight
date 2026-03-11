package net.maiatoday.moodsnap.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.maiatoday.moodsnap.data.MoodRepository
import net.maiatoday.moodsnap.data.OfflineMoodRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMoodRepository(
        offlineMoodRepository: OfflineMoodRepository
    ): MoodRepository
}
