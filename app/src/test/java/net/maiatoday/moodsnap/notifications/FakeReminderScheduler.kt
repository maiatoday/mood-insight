package net.maiatoday.moodsnap.notifications

class FakeReminderScheduler : IReminderScheduler {
    
    var scheduledHour: Int? = null
        private set
    var scheduledMinute: Int? = null
        private set
    var isCanceled: Boolean = false
        private set

    override fun scheduleReminder(hour: Int, minute: Int) {
        scheduledHour = hour
        scheduledMinute = minute
        isCanceled = false
    }

    override fun cancelReminder() {
        scheduledHour = null
        scheduledMinute = null
        isCanceled = true
    }
}
