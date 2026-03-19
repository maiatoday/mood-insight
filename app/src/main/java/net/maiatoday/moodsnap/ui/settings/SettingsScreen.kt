package net.maiatoday.moodsnap.ui.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import net.maiatoday.moodsnap.data.UserPreferences
import java.util.Locale
import androidx.compose.material3.Card

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()

    // Accompanist Permissions API for Android 13+ Notification Permission
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    // Keep track of whether the user intent was to enable the reminder
    // so we can enable it automatically once permission is granted.
    var pendingReminderEnable by remember { mutableStateOf(false) }

    // Listen for permission status changes
    LaunchedEffect(notificationPermissionState?.status) {
        if (notificationPermissionState?.status?.isGranted == true && pendingReminderEnable) {
            viewModel.toggleReminder(true)
            pendingReminderEnable = false
        }
    }

    SettingsContent(
        modifier = modifier,
        userPreferences = userPreferences,
        onBack = onBack,
        onToggleReminder = { isChecked ->
            if (isChecked) {
                if (notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
                    // Request permission if not granted on Android 13+
                    pendingReminderEnable = true
                    notificationPermissionState.launchPermissionRequest()
                } else {
                    // Already granted or Android < 13
                    viewModel.toggleReminder(true)
                }
            } else {
                // Turning it off
                pendingReminderEnable = false
                viewModel.toggleReminder(false)
            }
        },
        onTimeSelected = { hour, minute ->
            viewModel.toggleReminder(userPreferences.reminderEnabled, hour, minute)
        },
        onGenerateData = { viewModel.generateSampleData() },
        onClearData = { viewModel.clearAllData() },
        onTestNotification = {
            if (notificationPermissionState == null || notificationPermissionState.status.isGranted) {
                 viewModel.testNotification()
            } else {
                 notificationPermissionState.launchPermissionRequest()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    userPreferences: UserPreferences,
    onBack: () -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onGenerateData: () -> Unit,
    onClearData: () -> Unit,
    onTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Notification Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Reminder",
                    style = MaterialTheme.typography.bodyLarge
                )

                Switch(
                    checked = userPreferences.reminderEnabled,
                    onCheckedChange = onToggleReminder
                )
            }

            // Time selection UI
            if (userPreferences.reminderEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reminder Time",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", userPreferences.reminderHour, userPreferences.reminderMinute),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showTimePicker) {
                ReminderTimePicker(
                    initialHour = userPreferences.reminderHour,
                    initialMinute = userPreferences.reminderMinute,
                    onConfirm = { hour, minute ->
                        onTimeSelected(hour, minute)
                        showTimePicker = false
                    },
                    onDismiss = { showTimePicker = false }
                )
            }


            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onTestNotification,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Test Notification Now")
            }

            Button(
                onClick = onGenerateData,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Test Data")
            }

            Button(
                onClick = onClearData,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear All Data")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
