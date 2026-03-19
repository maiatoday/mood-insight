package net.maiatoday.moodsnap.ui.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.maiatoday.moodsnap.data.FakeMoodRepository
import net.maiatoday.moodsnap.data.FakeUserPreferenceRepository
import net.maiatoday.moodsnap.domain.ScheduleReminderUseCase
import net.maiatoday.moodsnap.notifications.FakeReminderScheduler
import net.maiatoday.moodsnap.notifications.INotificationHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Create a Fake for the NotificationHelper
class FakeNotificationHelper : INotificationHelper {
    var notificationShown = false
        private set

    override fun showReminderNotification() {
        notificationShown = true
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: FakeMoodRepository
    private lateinit var userPreferenceRepository: FakeUserPreferenceRepository
    private lateinit var reminderScheduler: FakeReminderScheduler
    private lateinit var scheduleReminderUseCase: ScheduleReminderUseCase
    private lateinit var fakeNotificationHelper: FakeNotificationHelper
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMoodRepository()
        userPreferenceRepository = FakeUserPreferenceRepository()
        reminderScheduler = FakeReminderScheduler()
        scheduleReminderUseCase = ScheduleReminderUseCase(userPreferenceRepository, reminderScheduler)
        
        fakeNotificationHelper = FakeNotificationHelper()

        viewModel = SettingsViewModel(repository, userPreferenceRepository, scheduleReminderUseCase, fakeNotificationHelper)
    }

    @Test
    fun `generateSampleData adds entries`() = runTest {
        assertTrue(repository.entriesList().isEmpty(), "Repository should be empty")
        
        viewModel.generateSampleData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(repository.entriesList().isNotEmpty(), "Repository should have entries")
    }

    @Test
    fun `clearAllData removes entries`() = runTest {
        repository.generateSampleData()
        assertTrue(repository.entriesList().isNotEmpty(), "Repository should have entries")
        
        viewModel.clearAllData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(repository.entriesList().isEmpty(), "Repository should be empty")
    }

    @Test
    fun `toggleReminder updates state and schedules reminder`() = runTest {
        // Initial state
        assertFalse(viewModel.userPreferences.value.reminderEnabled)

        // Toggle on
        viewModel.toggleReminder(true, 15, 30)
        testDispatcher.scheduler.advanceUntilIdle()

        // Check view model state
        val prefs = viewModel.userPreferences.value
        assertTrue(prefs.reminderEnabled)
        assertEquals(15, prefs.reminderHour)
        assertEquals(30, prefs.reminderMinute)

        // Check repository
        val repoPrefs = userPreferenceRepository.preferencesFlow.first()
        assertTrue(repoPrefs.reminderEnabled)
        assertEquals(15, repoPrefs.reminderHour)
        assertEquals(30, repoPrefs.reminderMinute)

        // Check scheduler
        assertEquals(15, reminderScheduler.scheduledHour)
        assertEquals(30, reminderScheduler.scheduledMinute)
        assertFalse(reminderScheduler.isCanceled)
    }

    @Test
    fun `toggleReminder off updates state and cancels reminder`() = runTest {
        // Toggle on first
        viewModel.toggleReminder(true, 15, 30)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Toggle off
        viewModel.toggleReminder(false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Check view model state
        val prefs = viewModel.userPreferences.value
        assertFalse(prefs.reminderEnabled)

        // Check repository
        val repoPrefs = userPreferenceRepository.preferencesFlow.first()
        assertFalse(repoPrefs.reminderEnabled)

        // Check scheduler
        assertTrue(reminderScheduler.isCanceled)
    }

    @Test
    fun `testNotification calls helper`() = runTest {
        viewModel.testNotification()
        assertTrue(fakeNotificationHelper.notificationShown)
    }
}
