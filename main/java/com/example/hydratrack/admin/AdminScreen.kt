package com.example.hydratrack.admin

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydratrack.data.AdminUserReport
import com.example.hydratrack.data.HydraRepository
import com.example.hydratrack.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = HydraRepository(app)
    private val prefs = PreferencesManager(app)

    private val _reports = MutableStateFlow<List<AdminUserReport>>(emptyList())
    val reports: StateFlow<List<AdminUserReport>> = _reports

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            // In a real app, we'd filter by prefs.analyticsConsentGiven
            _reports.value = repo.compileAdminReport()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val reports by viewModel.reports.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reports) { report ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("User: ${report.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Intake: ${report.totalIntakeMl}ml / ${report.dailyGoalMl}ml")
                        Text("Shakes: ${report.shakeCount}")
                        if (report.goalAchieved) {
                            Text("Goal Achieved! 🎉", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
