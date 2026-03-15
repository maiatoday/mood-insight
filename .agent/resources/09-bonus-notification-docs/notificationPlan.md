This plan outlines the implementation of a daily mood logging reminder using AlarmManager (Inexact) and Jetpack DataStore, following modern Android architecture.

Permissions & Manifest Requirements
• android.permission.POST_NOTIFICATIONS: Required for Android 13+ (API 33) to show the reminder.
• android.permission.RECEIVE_BOOT_COMPLETED: Required to reschedule alarms after a device reboot.
• android.permission.SCHEDULE_EXACT_ALARM: Not required as we are using inexact alarms to respect system battery optimizations.

Implementation Plan
Phase 1: Data Persistence (DataStore)
Create a UserPreferencesRepository using Jetpack DataStore to persist reminder settings.
• Fields: reminder_enabled (Boolean), reminder_hour (Int), reminder_minute (Int).
• Flows: Expose a preferencesFlow to the ViewModel.

Phase 2: Alarm Scheduling Logic
Create a ReminderScheduler utility class to encapsulate AlarmManager interactions.
• Use alarmManager.setInexactRepeating() with AlarmManager.RTC_WAKEUP.
• Target a BroadcastReceiver via a PendingIntent.
• Note: Use PendingIntent.FLAG_IMMUTABLE for security.

Phase 3: Notification & Broadcast Receiver
Implement the ReminderReceiver : BroadcastReceiver.
• Notification Channel: Initialize a dedicated "Reminders" channel (required for API 26+).
• Notification Construction: Use NotificationCompat.Builder with a deep link to the mood logging screen.
• Trigger logic: When onReceive is called, check DataStore to verify the reminder is still enabled before posting the notification.

Phase 4: Reliability & Maintenance
Ensure the reminder survives system events.
• Boot Recovery: Implement a RebootReceiver that listens for ACTION_BOOT_COMPLETED. It should read the scheduled time from DataStore and call the ReminderScheduler to re-register the alarm.
• Timezone Changes: (Optional but recommended) Listen for ACTION_TIMEZONE_CHANGED to ensure the alarm aligns with the user's local time.

Phase 5: Architecture Integration (UI/ViewModel)
• ViewModel: Create a SettingsViewModel that provides a ToggleReminder function.
• UseCase: Create a ScheduleReminderUseCase that the ViewModel calls. This UseCase will update DataStore and trigger the ReminderScheduler.
❗️ Critical Note for Execution: Ensure the notification logic checks for the POST_NOTIFICATIONS permission at runtime before attempting to show the notification, or the system will silently drop the intent on API 33+.