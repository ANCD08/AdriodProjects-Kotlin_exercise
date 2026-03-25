package com.example.hydratrack.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydratrack.notifications.ReminderManager
import com.example.hydratrack.utils.Constants
import com.example.hydratrack.utils.PreferencesManager

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    val prefs = PreferencesManager(app)

    var servingMl by mutableIntStateOf(prefs.servingMl)
    var reminderInterval by mutableIntStateOf(prefs.reminderIntervalHours)
    var darkMode by mutableStateOf(prefs.darkMode)
    var analyticsConsent by mutableStateOf(prefs.analyticsConsentGiven)
    var shakeSensitivity by mutableFloatStateOf(prefs.shakeSensitivity)

    fun saveServing(ml: Int) {
        servingMl = ml
        prefs.servingMl = ml
    }

    fun saveReminderInterval(hours: Int) {
        reminderInterval = hours
        prefs.reminderIntervalHours = hours
        ReminderManager.schedule(getApplication(), hours)
    }

    fun saveDarkMode(enabled: Boolean) {
        darkMode = enabled
        prefs.darkMode = enabled
    }

    fun saveConsent(given: Boolean) {
        analyticsConsent = given
        prefs.analyticsConsentGiven = given
    }

    fun saveShakeSensitivity(value: Float) {
        shakeSensitivity = value
        prefs.shakeSensitivity = value
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                SettingsSection(title = "💧 Serving Size") {
                    Text("Choose how much water one shake/tap logs:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Constants.SERVING_OPTIONS.forEach { option ->
                            FilterChip(
                                selected = viewModel.servingMl == option,
                                onClick = { viewModel.saveServing(option) },
                                label = { Text("${option}ml") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "📳 Shake Sensitivity") {
                    val sensitivityLabel = when {
                        viewModel.shakeSensitivity <= 9f -> "High (Easy to trigger)"
                        viewModel.shakeSensitivity <= 13f -> "Medium (Recommended)"
                        else -> "Low (Harder to trigger)"
                    }
                    Text(sensitivityLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Slider(
                        value = viewModel.shakeSensitivity,
                        onValueChange = { viewModel.saveShakeSensitivity(it) },
                        valueRange = 8f..18f,
                        steps = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("High", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Low", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }

            item {
                SettingsSection(title = "🔔 Reminders") {
                    val options = listOf(0 to "Off", 1 to "Every 1h", 2 to "Every 2h",
                        3 to "Every 3h", 4 to "Every 4h")
                    options.forEach { (hours, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                            RadioButton(
                                selected = viewModel.reminderInterval == hours,
                                onClick = { viewModel.saveReminderInterval(hours) }
                            )
                        }
                        if (hours != 4) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }

            item {
                SettingsSection(title = "🎨 Appearance") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = viewModel.darkMode,
                            onCheckedChange = {
                                viewModel.saveDarkMode(it)
                                onThemeToggle(it)
                            }
                        )
                    }
                }
            }

            item {
                SettingsSection(title = "🔒 Privacy & Analytics") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "All your data stays on this device. The analytics toggle only affects " +
                            "whether the local admin panel can include your anonymized stats.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Allow local analytics", style = MaterialTheme.typography.bodyLarge)
                                Text("Opt-in — off by default",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                            Switch(
                                checked = viewModel.analyticsConsent,
                                onCheckedChange = { viewModel.saveConsent(it) }
                            )
                        }
                    }
                }
            }

            item {
                TextButton(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("App Version 1.0.0", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            content()
        }
    }
}
