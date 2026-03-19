package net.maiatoday.moodsnap.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import net.maiatoday.moodsnap.MainActivity
import net.maiatoday.moodsnap.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) : INotificationHelper {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for mood logging reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showReminderNotification() {
        createNotificationChannel()

        val deepLinkUri = Uri.parse("moodsnap://add_edit?entryId=null")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri, context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("How are you feeling?")
            .setContentText("Time to log your mood.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with a proper icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "mood_reminder_channel"
        const val NOTIFICATION_ID = 1
    }
}
