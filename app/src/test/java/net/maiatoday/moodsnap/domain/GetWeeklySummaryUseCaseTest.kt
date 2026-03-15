package net.maiatoday.moodsnap.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.maiatoday.moodsnap.data.FakeMoodRepository
import net.maiatoday.moodsnap.data.MoodEntry
import net.maiatoday.moodsnap.data.Tag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar

class GetWeeklySummaryUseCaseTest {

    private lateinit var moodRepository: FakeMoodRepository
    private lateinit var resonanceEngine: ResonanceEngine
    private lateinit var getWeeklySummaryUseCase: GetWeeklySummaryUseCase

    @BeforeEach
    fun setUp() {
        moodRepository = FakeMoodRepository()
        resonanceEngine = ResonanceEngine()
        getWeeklySummaryUseCase = GetWeeklySummaryUseCase(moodRepository, resonanceEngine)
    }

    @Test
    fun `invoke should return summary of entries from the last 7 days`() = runTest {
        // Given
        val now = Calendar.getInstance()
        
        // Entry 1: Today
        val entryToday = MoodEntry(
            moodScore = 5,
            energy = 4,
            movement = true,
            sunlight = true,
            sleep = true,
            timestamp = now.time,
            notes = "Today"
        )
        
        // Entry 2: 3 days ago
        val threeDaysAgo = Calendar.getInstance()
        threeDaysAgo.add(Calendar.DAY_OF_YEAR, -3)
        val entry3DaysAgo = MoodEntry(
            moodScore = 3,
            energy = 2,
            movement = false,
            sunlight = true,
            sleep = false,
            timestamp = threeDaysAgo.time,
            notes = "3 days ago"
        )

        // Entry 3: 10 days ago (should be excluded)
        val tenDaysAgo = Calendar.getInstance()
        tenDaysAgo.add(Calendar.DAY_OF_YEAR, -10)

        val id1 = moodRepository.insert(entryToday).toInt()
        val id2 = moodRepository.insert(entry3DaysAgo).toInt()

        val tagHappy = "Happy"
        val tagCalm = "Calm"
        moodRepository.insertTag(Tag(tagHappy))
        moodRepository.insertTag(Tag(tagCalm))
        moodRepository.addTagToEntry(id1, tagHappy)
        moodRepository.addTagToEntry(id2, tagCalm)

        // When
        val summary = getWeeklySummaryUseCase().first()

        // Then
        assertEquals(4.0f, summary.averageMood) // (5 + 3) / 2
        assertEquals(3.0f, summary.averageEnergy) // (4 + 2) / 2
        assertTrue(summary.resonance > 4.0f) // Recent mood (5) weighted more than old mood (3)
        assertEquals(1, summary.movementCount) // Only entryToday
        assertEquals(2, summary.sunlightCount) // Both entryToday and entry3DaysAgo
        assertEquals(1, summary.sleepCount) // Only entryToday
        assertEquals(2, summary.tags.size)
        assertTrue(summary.tags.contains(tagHappy))
        assertTrue(summary.tags.contains(tagCalm))
        assertEquals(2, summary.dailyMoods.size)
        assertEquals(Mood.GREAT, summary.currentMood)
    }

    @Test
    fun `invoke should return empty summary when no entries exist`() = runTest {
        // When
        val summary = getWeeklySummaryUseCase().first()

        // Then
        assertEquals(0f, summary.averageMood)
        assertEquals(0f, summary.averageEnergy)
        assertEquals(0, summary.movementCount)
        assertEquals(0, summary.sunlightCount)
        assertEquals(0, summary.sleepCount)
        assertTrue(summary.tags.isEmpty())
        assertTrue(summary.dailyMoods.isEmpty())
        assertEquals(null, summary.currentMood)
    }

    @Test
    fun `invoke should handle multiple entries on the same day by averaging them`() = runTest {
        // Given
        val now = Calendar.getInstance()
        val entry1 = MoodEntry(
            moodScore = 5,
            timestamp = now.time,
            notes = "Morning",
            movement = false,
            sunlight = false,
            sleep = false,
            energy = 0
        )
        val entry2 = MoodEntry(
            moodScore = 1,
            timestamp = now.time,
            notes = "Evening",
            movement = false,
            sunlight = false,
            sleep = false,
            energy = 0
        )
        
        moodRepository.insert(entry1)
        moodRepository.insert(entry2)

        // When
        val summary = getWeeklySummaryUseCase().first()

        // Then
        assertEquals(3.0f, summary.averageMood)
        assertEquals(1, summary.dailyMoods.size)
        assertEquals(3, summary.dailyMoods[0].mood.score)
    }
}
