package net.maiatoday.moodsnap.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MoodEntryWithTags(
    @Embedded val moodEntry: MoodEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "name",
        associateBy = Junction(
            value = MoodEntryTagCrossRef::class,
            parentColumn = "moodEntryId",
            entityColumn = "tagName"
        )
    )
    val tags: List<Tag>
)
