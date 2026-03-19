package net.maiatoday.moodsnap.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class UserPreferences(
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)

interface UserPreferenceRepository {
    val preferencesFlow: Flow<UserPreferences>
    suspend fun updateReminderEnabled(enabled: Boolean)
    suspend fun updateReminderTime(hour: Int, minute: Int)
}

@Singleton
class DefaultUserPreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferenceRepository {

    override val preferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val reminderEnabled = preferences[PreferencesKeys.REMINDER_ENABLED] ?: false
            val reminderHour = preferences[PreferencesKeys.REMINDER_HOUR] ?: 20 // Default to 8 PM
            val reminderMinute = preferences[PreferencesKeys.REMINDER_MINUTE] ?: 0 // Default to 8:00 PM

            UserPreferences(reminderEnabled, reminderHour, reminderMinute)
        }

    override suspend fun updateReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_ENABLED] = enabled
        }
    }

    override suspend fun updateReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_HOUR] = hour
            preferences[PreferencesKeys.REMINDER_MINUTE] = minute
        }
    }

    private object PreferencesKeys {
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    }
}
