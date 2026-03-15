package net.maiatoday.moodsnap.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant
import java.time.Duration

class ResonanceEngineTest {

    private lateinit var engine: ResonanceEngine

    @BeforeEach
    fun setup() {
        engine = ResonanceEngine()
    }

    private fun createEntry(score: Int, daysAgo: Int): MoodEntryDomain {
        val now = Instant.now()
        val timestamp = now.minus(Duration.ofDays(daysAgo.toLong()))
        return MoodEntryDomain(
            id = 0,
            mood = Mood.fromScore(score),
            notes = "Test entry",
            movement = false,
            sunlight = false,
            sleep = false,
            energy = 0,
            timestamp = timestamp,
            tags = emptyList()
        )
    }

    @Test
    fun `empty list returns zero`() {
        val result = engine.compute(emptyList())
        assertEquals(0.0, result, 0.001)
    }

    @ParameterizedTest(name = "Score {0} from {1} days ago returns {0}")
    @CsvSource(
        "1, 0",
        "5, 0",
        "3, 10",
        "4, 30"
    )
    fun `single entry returns its own score`(score: Int, daysAgo: Int) {
        val entry = createEntry(score, daysAgo)
        val result = engine.compute(listOf(entry))
        assertEquals(score.toDouble(), result, 0.001)
    }

    @ParameterizedTest(name = "Multiple entries with score {0} return {0}")
    @ValueSource(ints = [1, 3, 5])
    fun `multiple entries with same score return that score`(score: Int) {
        val entries = listOf(
            createEntry(score, 0),
            createEntry(score, 5),
            createEntry(score, 10)
        )
        val result = engine.compute(entries)
        assertEquals(score.toDouble(), result, 0.001)
    }

    @Test
    fun `recent entries have more weight than older entries`() {
        // Simple average of 1 and 5 is 3.0
        val oldEntry = createEntry(1, 10)
        val newEntry = createEntry(5, 0)

        val result = engine.compute(listOf(oldEntry, newEntry))

        // Because of exponential decay, the result should be weighted towards the recent 5
        assertTrue(result > 3.0, "Resonance ($result) should be closer to recent mood (5) than old mood (1)")
    }

    @ParameterizedTest(name = "Old score {0} vs new score {1} results in resonance shifted towards {1}")
    @CsvSource(
        "1, 5",
        "5, 1"
    )
    fun `resonance follows the trend of recent moods`(oldScore: Int, newScore: Int) {
        val oldEntry = createEntry(oldScore, 10)
        val newEntry = createEntry(newScore, 0)
        val average = (oldScore + newScore) / 2.0

        val result = engine.compute(listOf(oldEntry, newEntry))

        if (newScore > oldScore) {
            assertTrue(result > average, "Resonance ($result) should be above average ($average) when trend is upward")
        } else {
            assertTrue(result < average, "Resonance ($result) should be below average ($average) when trend is downward")
        }
    }

    @Test
    fun `very old entries have negligible impact`() {
        val veryOldEntry = createEntry(1, 100) // 100 days ago
        val recentEntry = createEntry(5, 0)    // Today

        val result = engine.compute(listOf(veryOldEntry, recentEntry))

        // With lambda 0.15, e^(-0.15 * 100) is basically 0.
        // The result should be very close to 5.0
        assertEquals(5.0, result, 0.05)
    }
}
