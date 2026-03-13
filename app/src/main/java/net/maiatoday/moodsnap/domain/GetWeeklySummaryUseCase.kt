package net.maiatoday.moodsnap.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.maiatoday.moodsnap.data.MoodEntryWithTags
import net.maiatoday.moodsnap.data.MoodRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
    val currentMood: Int? = null
)

data class DailyMood(
    val dayLabel: String,
    val score: Int
)

class GetWeeklySummaryUseCase @Inject constructor(
    private val moodRepository: MoodRepository,
    private val resonanceEngine: ResonanceEngine
) {
    operator fun invoke(): Flow<WeeklySummary> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val sevenDaysAgo = calendar.time

        return moodRepository.getEntriesWithTagsFromDate(sevenDaysAgo).map { entriesWithTags ->
            calculateSummary(entriesWithTags)
        }
    }

    private fun calculateSummary(entriesWithTags: List<MoodEntryWithTags>): WeeklySummary {
        if (entriesWithTags.isEmpty()) return WeeklySummary()

        val entries = entriesWithTags.map { it.moodEntry }
        
        val averageMood = entries.map { it.moodScore }.average().toFloat()
        val averageEnergy = entries.map { it.energy }.average().toFloat()
        val resonance = resonanceEngine.compute(entries).toFloat()
        
        val movementCount = entries.count { it.movement }
        val sunlightCount = entries.count { it.sunlight }
        val sleepCount = entries.count { it.sleep }
        
        val tags = entriesWithTags.flatMap { it.tags }.map { it.name }.distinct()
        
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        
        val dailyMoods = entries
            .groupBy { 
                val cal = Calendar.getInstance()
                cal.time = it.timestamp
                cal.get(Calendar.DAY_OF_YEAR) to cal.get(Calendar.YEAR)
            }
            .map { (_, dailyEntries) ->
                val avgScore = dailyEntries.map { it.moodScore }.average().toInt()
                val firstEntry = dailyEntries.first()
                val dayLabel = dateFormat.format(firstEntry.timestamp)
                firstEntry.timestamp.time to DailyMood(dayLabel, avgScore)
            }
            .sortedBy { it.first }
            .map { it.second }

        val currentMood = entries.maxByOrNull { it.timestamp.time }?.moodScore
        
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
