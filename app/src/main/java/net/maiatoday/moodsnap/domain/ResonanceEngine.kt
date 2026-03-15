package net.maiatoday.moodsnap.domain

import java.time.Instant
import java.time.Duration
import kotlin.math.exp
import javax.inject.Inject

/**
 * ResonanceEngine calculates the "Emotional Resonance" of a user based on a list of mood entries.
 * It uses an exponential decay model where more recent moods have a higher weight than older ones.
 * This provides a more dynamic view of the user's emotional state compared to a simple average.
 */
class ResonanceEngine @Inject constructor() {

    // The decay constant. 
    // Higher values mean older moods are forgotten faster.
    private val lambda = 0.15 

    /**
     * Calculates the "Emotional Resonance" of a user based on a list of mood entries.
     * Uses a weighted average where weights decay exponentially over time.
     * 
     * @param entries The list of mood entries to process.
     * @return A double representing the resonance (weighted average score).
     */
    fun compute(entries: List<MoodEntryDomain>): Double {
        if (entries.isEmpty()) return 0.0

        val now = Instant.now()
        var totalWeight = 0.0
        var weightedSum = 0.0

        for (entry in entries) {
            // Calculate time difference in days
            val duration = Duration.between(entry.timestamp, now)
            val days = duration.toDays().toDouble()
            
            // Exponential decay formula: w = e^(-lambda * days)
            val weight = exp(-lambda * days)
            
            weightedSum += entry.mood.score * weight
            totalWeight += weight
        }

        return if (totalWeight > 0) weightedSum / totalWeight else 0.0
    }
}
