package net.maiatoday.moodsnap.domain

import net.maiatoday.moodsnap.data.MoodEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class ResonanceEngineTest {

    private lateinit var engine: ResonanceEngine

    @Before
    fun setup() {
        engine = ResonanceEngine()
    }

    @Test
    fun `compute should return zero when given an empty list of mood entries`() {
        val result = engine.compute(emptyList())
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `compute should result in a score closer to recent entries due to exponential decay`() {

        val now = System.currentTimeMillis()
        
        // 10 days ago, score 1
        val tenDaysAgo = Date(now - (1000L * 60 * 60 * 24 * 10))
        val oldEntry = MoodEntry(
            moodScore = 1,
            notes = "Old entry",
            movement = false,
            sunlight = false,
            sleep = false,
            timestamp = tenDaysAgo
        )
        
        // Now, score 5
        val newEntry = MoodEntry(
            moodScore = 5,
            notes = "New entry",
            movement = false,
            sunlight = false,
            sleep = false,
            timestamp = Date(now)
        )

        val result = engine.compute(listOf(oldEntry, newEntry))
        
        // Because of exponential decay, the result should be closer to 5 than 1.
        // A simple average would be 3.0.
        assertTrue("Resonance ($result) should be significantly greater than simple average (3.0)", result > 3.0)
    }

    @Test
    fun `compute should return the exact mood score when only one entry is provided`() {

        val now = System.currentTimeMillis()
        
        // A single score of 5 from 10 days ago
        val tenDaysAgo = Date(now - (1000L * 60 * 60 * 24 * 10))
        val oldEntry = MoodEntry(
            moodScore = 5,
            notes = "Old",
            movement = false,
            sunlight = false,
            sleep = false,
            timestamp = tenDaysAgo
        )

        val result = engine.compute(listOf(oldEntry))
        
        // For a single entry, the weighted average is just the score itself
        assertEquals(5.0, result, 0.001)
    }
}
