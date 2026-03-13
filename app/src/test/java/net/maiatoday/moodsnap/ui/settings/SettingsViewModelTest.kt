package net.maiatoday.moodsnap.ui.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.maiatoday.moodsnap.data.FakeMoodRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: FakeMoodRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMoodRepository()
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun `generateSampleData adds entries`() = runTest {
        assertTrue("Repository should be empty", repository.entriesList().isEmpty())
        
        viewModel.generateSampleData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue("Repository should have entries", repository.entriesList().isNotEmpty())
    }

    @Test
    fun `clearAllData removes entries`() = runTest {
        repository.generateSampleData()
        assertTrue("Repository should have entries", repository.entriesList().isNotEmpty())
        
        viewModel.clearAllData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue("Repository should be empty", repository.entriesList().isEmpty())
    }
}
