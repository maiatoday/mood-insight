package net.maiatoday.moodsnap.ui.addedit

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.maiatoday.moodsnap.data.FakeMoodRepository
import net.maiatoday.moodsnap.data.MoodEntry
import net.maiatoday.moodsnap.data.Tag
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {

    private lateinit var viewModel: AddEditViewModel
    private lateinit var repository: FakeMoodRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMoodRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default for new entry`() = runTest {
        viewModel = AddEditViewModel(repository, SavedStateHandle())
        val state = viewModel.uiState.value
        assertEquals(0, state.moodScore)
        assertTrue(state.tags.isEmpty())
        assertTrue(state.notes.isEmpty())
        assertFalse(state.isEntrySaved)
    }

    @Test
    fun `loading existing entry populates state`() = runTest {
        // Given an existing entry in the repository
        val timestamp = Date(1000)
        val entry = MoodEntry(
            id = 1,
            moodScore = 5,
            notes = "Feeling great",
            movement = true,
            sunlight = true,
            sleep = true,
            energy = 10,
            timestamp = timestamp
        )
        repository.insert(entry)
        repository.insertTag(Tag("Happy"))
        repository.addTagToEntry(1, "Happy")

        // When initializing ViewModel with entryId
        viewModel = AddEditViewModel(repository, SavedStateHandle(mapOf("entryId" to 1)))
        advanceUntilIdle()

        // Then state should match the entry
        val state = viewModel.uiState.value
        assertEquals(5, state.moodScore)
        assertEquals("Feeling great", state.notes)
        assertTrue(state.movement)
        assertEquals(10, state.energy)
        assertEquals(listOf("Happy"), state.tags)
    }

    @Test
    fun `updates mood score`() = runTest {
        viewModel = AddEditViewModel(repository, SavedStateHandle())
        viewModel.onMoodScoreChange(4)
        assertEquals(4, viewModel.uiState.value.moodScore)
    }

    @Test
    fun `updates notes`() = runTest {
        viewModel = AddEditViewModel(repository, SavedStateHandle())
        viewModel.onNotesChange("New note")
        assertEquals("New note", viewModel.uiState.value.notes)
    }

    @Test
    fun `tag management - add, remove, and create`() = runTest {
        viewModel = AddEditViewModel(repository, SavedStateHandle())
        
        // Add tag
        viewModel.addTag("Work")
        assertTrue(viewModel.uiState.value.tags.contains("Work"))

        // Remove tag
        viewModel.removeTag("Work")
        assertFalse(viewModel.uiState.value.tags.contains("Work"))

        // Create new tag (should interact with repository)
        viewModel.createNewTag("Energy")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.tags.contains("Energy"))
    }

    @Test
    fun `habit and energy changes update state`() = runTest {
        viewModel = AddEditViewModel(repository, SavedStateHandle())
        
        viewModel.onMovementChange(true)
        assertTrue(viewModel.uiState.value.movement)

        viewModel.onSleepChange(true)
        assertTrue(viewModel.uiState.value.sleep)

        viewModel.onSunlightChange(true)
        assertTrue(viewModel.uiState.value.sunlight)

        viewModel.onEnergyChange(12)
        assertEquals(12, viewModel.uiState.value.energy)
    }

    @Test
    fun `saveEntry inserts new entry and updates tags`() = runTest {
        viewModel = AddEditViewModel(repository, SavedStateHandle())
        viewModel.onMoodScoreChange(3)
        viewModel.onNotesChange("New Entry")
        viewModel.addTag("TestTag")
        
        viewModel.saveEntry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEntrySaved)
        
        // Verify repository has the entry
        val entries = repository.entriesList()
        assertEquals(1, entries.size)
        assertEquals("New Entry", entries[0].notes)
    }

    @Test
    fun `saveEntry updates existing entry`() = runTest {
        val entry = MoodEntry(id = 1, moodScore = 1, notes = "Old", movement = false, sunlight = false, sleep = false)
        repository.insert(entry)
        
        viewModel = AddEditViewModel(repository, SavedStateHandle(mapOf("entryId" to 1)))
        advanceUntilIdle()
        
        viewModel.onMoodScoreChange(5)
        viewModel.onNotesChange("Updated")
        viewModel.saveEntry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEntrySaved)
        val entries = repository.entriesList()
        assertEquals(1, entries.size)
        assertEquals("Updated", entries[0].notes)
        assertEquals(5, entries[0].moodScore)
    }
}
