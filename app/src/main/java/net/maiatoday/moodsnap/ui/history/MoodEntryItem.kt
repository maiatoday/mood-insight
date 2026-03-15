package net.maiatoday.moodsnap.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.maiatoday.moodsnap.domain.MoodEntryDomain
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MoodEntryItem(entryDomain: MoodEntryDomain, onClick: () -> Unit) {
    val mood = entryDomain.mood
    val timestamp = entryDomain.timestamp.atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(mood.color)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mood.emoji,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(timestamp)
                    Text("Description: ${mood.description}")
                    Text("Score: ${mood.score}")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Label,
                            contentDescription = "Tags"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(entryDomain.tags.joinToString())
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Notes"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(entryDomain.notes)
                    }
                    Text("Energy: ${entryDomain.energy}")
                    Text("Sleep: ${entryDomain.sleep}")
                    Text("Movement: ${entryDomain.movement}")
                    Text("Sunlight: ${entryDomain.sunlight}")
                }
            }
        }
    }
}
