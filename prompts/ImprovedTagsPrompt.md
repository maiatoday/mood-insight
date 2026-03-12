# Improved Prompt for Phase 3: UI/UX Implementation

## Context
We have successfully refactored the database to support a many-to-many relationship between `MoodEntry` and `Tag`. The `MoodRepository` has been updated to expose methods for fetching tags and managing the relationship. Now we need to implement the UI for managing tags in the `AddEditScreen`.

## Goal
Implement a user-friendly tag management interface within the `AddEditScreen`.

## Requirements

### 1. ViewModel Updates (`AddEditViewModel`)
*   **Dependency**: Inject `MoodRepository` instead of `MoodEntryDao`.
*   **State**: 
    *   Expose a list of *all available tags* (to suggest to the user).
    *   Expose the list of *selected tags* for the current entry.
*   **Logic**:
    *   `loadEntry(id)`: Should now use `repository.getEntryWithTagsById(id)` to populate the state.
    *   `saveEntry()`: 
        *   Save the `MoodEntry` first (insert or update).
        *   Then, iterate through selected tags and update the cross-reference table using `repository.addTagToEntry`.
        *   Handle tag creation: If a user enters a new tag, create it using `repository.insertTag` before linking it.
    *   `addTag(tagName)`: Add to the local "selected" state.
    *   `removeTag(tagName)`: Remove from the local "selected" state.
    *   `createTag(tagName)`: Create a new tag in the database immediately (or wait until save? - *Decision: Create immediately for simpler UX to populate the suggestion list*).

### 2. UI Components (`AddEditScreen`)
*   **Tag Display**: Use a `FlowRow` to display selected tags as `InputChip`s.
    *   Each chip should have a "trailing icon" (X) to remove it.
*   **Tag Addition**:
    *   Add an "Add Tag" button (Icon + Text).
    *   When clicked, show a **Dialog** or **ModalBottomSheet**.
    *   **Dialog Content**:
        *   `OutlinedTextField` for typing a new tag name.
        *   `LazyColumn` or `FlowRow` showing *all available tags* as `FilterChip`s.
        *   Clicking an available tag adds it to the selection and closes the dialog.
        *   Typing a name and hitting "Done" (or a specific "Create" button) creates the new tag, adds it, and closes the dialog.

## Step-by-Step Implementation Plan
1.  **Refactor ViewModel**: Switch to Repository, update state to hold `selectedTags` (List<Tag>) and `availableTags` (List<Tag>).
2.  **Create `TagSelectionDialog`**: A new Composable for the selection/creation logic.
3.  **Update `AddEditScreen`**: Replace the current placeholder tag UI with the chip group and the "Add" button that triggers the dialog.

## Request
Please generate the Kotlin code for:
1.  The updated `AddEditViewModel`.
2.  The new `TagSelectionDialog` composable.
3.  The updated `AddEditScreen` integration.
