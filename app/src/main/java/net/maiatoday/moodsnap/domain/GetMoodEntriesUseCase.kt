package net.maiatoday.moodsnap.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.maiatoday.moodsnap.data.MoodRepository
import javax.inject.Inject

class GetMoodEntriesUseCase @Inject constructor(
    private val moodRepository: MoodRepository
) {
    operator fun invoke(): Flow<List<MoodEntryDomain>> = moodRepository.getAllEntriesWithTags().map { list ->
        list.map { it.toDomain() }
    }
}