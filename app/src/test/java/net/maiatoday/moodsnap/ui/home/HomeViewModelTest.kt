package net.maiatoday.moodsnap.ui.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.maiatoday.moodsnap.data.FakeMoodRepository
import net.maiatoday.moodsnap.domain.GetWeeklySummaryUseCase
import net.maiatoday.moodsnap.domain.ResonanceEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: FakeMoodRepository
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var weeklySummaryUseCase: GetWeeklySummaryUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMoodRepository()
    }

    @Test
    fun `initial state is null for new entry`() = runTest {
        weeklySummaryUseCase = GetWeeklySummaryUseCase(repository, ResonanceEngine())
        viewModel = HomeViewModel(weeklySummaryUseCase)
        
        // Start collection in the background to activate WhileSubscribed
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.summary.collect()
        }

        val state = viewModel.summary.value
        assertTrue("State should be null", state == null)
        collectJob.cancel()
    }

    @Test
    fun `summary state with a week of data`() = runTest {
        repository.generateTestData(7)
        weeklySummaryUseCase = GetWeeklySummaryUseCase(repository, ResonanceEngine())
        viewModel = HomeViewModel(weeklySummaryUseCase)

        // Start collection in the background to activate WhileSubscribed
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.summary.collect()
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.summary.value
        assertTrue("State should not be null", state != null)
        assertEquals("Expected 8 days in weekly summary (7 days ago to today)", 8, state!!.dailyMoods.size)
    }
}
