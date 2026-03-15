package net.maiatoday.moodsnap.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.maiatoday.moodsnap.data.MoodRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

data class WeeklySummary(
    val averageMood: Float = 0f,
    val averageEnergy: Float = 0f,
    val resonance: Float = 0f,
    val movementCount: Int = 0,
    val sunlightCount: Int = 0,
    val sleepCount: Int = 0,
    val tags: List<String> = emptyList(),
    val dailyMoods: List<DailyMood> = emptyList(),
    val currentMood: Mood? = null
)

data class DailyMood(
    val dayLabel: String,
    val mood: Mood
)

class GetWeeklySummaryUseCase @Inject constructor(
    private val moodRepository: MoodRepository,
    private val resonanceEngine: ResonanceEngine
) {
    operator fun invoke(): Flow<WeeklySummary> {
        val now = Instant.now()
        val sevenDaysAgo = now.minus(7, ChronoUnit.DAYS)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
        
        val startDate = Date.from(sevenDaysAgo)

        return moodRepository.getEntriesWithTagsFromDate(startDate).map { entriesWithTags ->
            val domainEntries = entriesWithTags.map { it.toDomain() }
            calculateSummary(domainEntries)
        }
    }

    private fun calculateSummary(entries: List<MoodEntryDomain>): WeeklySummary {
        if (entries.isEmpty()) return WeeklySummary()

        val averageMood = entries.map { it.mood.score }.average().toFloat()
        val averageEnergy = entries.map { it.energy }.average().toFloat()
        
        // Note: ResonanceEngine might still need raw MoodEntry if it's not refactored yet.
        // If resonanceEngine.compute(entries) fails, we'd need to map back or refactor the engine.
        // Assuming resonanceEngine.compute handles domain objects now or we will refactor it.
        val resonance = resonanceEngine.compute(entries).toFloat()
        
        val movementCount = entries.count { it.movement }
        val sunlightCount = entries.count { it.sunlight }
        val sleepCount = entries.count { it.sleep }
        
        val tags = entries.flatMap { it.tags }.distinct()
        
        val dayFormatter = DateTimeFormatter.ofPattern("EEE")
        val zoneId = ZoneId.systemDefault()
        
        val dailyMoods = entries
            .groupBy { it.timestamp.atZone(zoneId).toLocalDate() }
            .map { (date, dailyEntries) ->
                val avgScore = dailyEntries.map { it.mood.score }.average().toInt()
                val mood = Mood.fromScore(avgScore)
                val dayLabel = date.format(dayFormatter)
                date to DailyMood(dayLabel, mood)
            }
            .sortedBy { it.first }
            .map { it.second }

        val currentMood = entries.maxByOrNull { it.timestamp }?.mood
        
        return WeeklySummary(
            averageMood = averageMood,
            averageEnergy = averageEnergy,
            resonance = resonance,
            movementCount = movementCount,
            sunlightCount = sunlightCount,
            sleepCount = sleepCount,
            tags = tags,
            dailyMoods = dailyMoods,
            currentMood = currentMood
        )
    }
}
