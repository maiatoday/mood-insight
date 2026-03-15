package net.maiatoday.moodsnap.domain

import androidx.compose.ui.graphics.Color
import net.maiatoday.moodsnap.ui.theme.MoodAwful
import net.maiatoday.moodsnap.ui.theme.MoodGood
import net.maiatoday.moodsnap.ui.theme.MoodGreat
import net.maiatoday.moodsnap.ui.theme.MoodMeh
import net.maiatoday.moodsnap.ui.theme.MoodOk


enum class Mood(val score: Int, val emoji: String, val color: Color, val description: String) {
    AWFUL(1, "😢", MoodAwful, "Awful"),
    MEH(2, "😐", MoodMeh, "Meh"),
    OK(3, "🙂", MoodOk, "OK"),
    GOOD(4, "😄", MoodGood, "Good"),
    GREAT(5, "🤩", MoodGreat, "Great");

    companion object {
        fun fromScore(score: Int): Mood {
            return entries.find { it.score == score } ?: OK
        }
    }
}
