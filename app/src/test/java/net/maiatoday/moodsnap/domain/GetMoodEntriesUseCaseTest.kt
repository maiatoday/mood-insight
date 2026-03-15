package net.maiatoday.moodsnap.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.maiatoday.moodsnap.data.FakeMoodRepository
import net.maiatoday.moodsnap.data.MoodEntry
import net.maiatoday.moodsnap.data.Tag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class GetMoodEntriesUseCaseTest {

    private lateinit var moodRepository: FakeMoodRepository
    private lateinit var getMoodEntriesUseCase: GetMoodEntriesUseCase

    @BeforeEach
    fun setUp() {
        moodRepository = FakeMoodRepository()
        getMoodEntriesUseCase = GetMoodEntriesUseCase(moodRepository)
    }

    @Test
    fun `invoke should return all entries with tags from repository mapped to domain`() = runTest {
        // Given
        val tag1 = Tag("Happy")
        val tag2 = Tag("Work")
        moodRepository.insertTag(tag1)
        moodRepository.insertTag(tag2)

        val entry1 = MoodEntry(moodScore = 5, notes = "Great day", movement = true, sunlight = true, sleep = true, timestamp = Date(1000))
        val entry2 = MoodEntry(moodScore = 3, notes = "Okay day", movement = false, sunlight = true, sleep = false, timestamp = Date(2000))
        
        val id1 = moodRepository.insert(entry1).toInt()
        val id2 = moodRepository.insert(entry2).toInt()
        
        moodRepository.addTagToEntry(id1, tag1.name)
        moodRepository.addTagToEntry(id2, tag2.name)

        // When
        val result = getMoodEntriesUseCase().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(id1, result[0].id)
        assertEquals(tag1.name, result[0].tags[0])
        assertEquals("Great", result[0].mood.description)
        assertEquals(id2, result[1].id)
        assertEquals(tag2.name, result[1].tags[0])
        assertEquals("OK", result[1].mood.description)
    }

    @Test
    fun `invoke should return empty list when repository is empty`() = runTest {
        // When
        val result = getMoodEntriesUseCase().first()

        // Then
        assertEquals(0, result.size)
    }
}