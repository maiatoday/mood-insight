package net.maiatoday.moodsnap.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.maiatoday.moodsnap.data.FakeUserPreferenceRepository
import net.maiatoday.moodsnap.notifications.FakeReminderScheduler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScheduleReminderUseCaseTest {

    private lateinit var userPreferenceRepository: FakeUserPreferenceRepository
    private lateinit var reminderScheduler: FakeReminderScheduler
    private lateinit var useCase: ScheduleReminderUseCase

    @BeforeEach
    fun setup() {
        userPreferenceRepository = FakeUserPreferenceRepository()
        reminderScheduler = FakeReminderScheduler()
        useCase = ScheduleReminderUseCase(userPreferenceRepository, reminderScheduler)
    }

    @Test
    fun `invoke with enabled=true updates preferences and schedules reminder`() = runTest {
        useCase.invoke(enabled = true, hour = 14, minute = 45)

        // Verify Preferences updated
        val prefs = userPreferenceRepository.preferencesFlow.first()
        assertTrue(prefs.reminderEnabled)
        assertEquals(14, prefs.reminderHour)
        assertEquals(45, prefs.reminderMinute)

        // Verify Scheduler called
        assertEquals(14, reminderScheduler.scheduledHour)
        assertEquals(45, reminderScheduler.scheduledMinute)
        assertFalse(reminderScheduler.isCanceled)
    }

    @Test
    fun `invoke with enabled=false updates preferences and cancels reminder`() = runTest {
        // First schedule one
        useCase.invoke(enabled = true, hour = 9, minute = 0)
        
        // Then cancel
        useCase.invoke(enabled = false, hour = 9, minute = 0)

        // Verify Preferences updated
        val prefs = userPreferenceRepository.preferencesFlow.first()
        assertFalse(prefs.reminderEnabled)

        // Verify Scheduler called
        assertTrue(reminderScheduler.isCanceled)
    }
}
