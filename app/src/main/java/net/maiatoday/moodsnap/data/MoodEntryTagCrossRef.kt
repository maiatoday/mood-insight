package net.maiatoday.moodsnap.data

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "mood_entry_tag_cross_ref",
    primaryKeys = ["moodEntryId", "tagName"],
    indices = [Index(value = ["tagName"]), Index(value = ["moodEntryId"])]
)
data class MoodEntryTagCrossRef(
    val moodEntryId: Int,
    val tagName: String
)
