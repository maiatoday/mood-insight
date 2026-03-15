package net.maiatoday.moodsnap.ui.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.maiatoday.moodsnap.data.FakeMoodRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: FakeMoodRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMoodRepository()
        viewModel = SettingsViewModel(repository)
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
}
