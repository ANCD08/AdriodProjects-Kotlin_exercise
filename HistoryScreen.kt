package com.example.hydratrack.ui.screens

import android.app.Application
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.hydratrack.data.dao.DailyTotal
import com.example.hydratrack.data.HydraRepository
import com.example.hydratrack.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = HydraRepository(app)
    private val prefs = PreferencesManager(app)

    private val _weeklyData = MutableStateFlow<List<DailyTotal>>(emptyList())
    val weeklyData: StateFlow<List<DailyTotal>> = _weeklyData

    init {
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            _weeklyData.value = repo.getWeeklyTotals(prefs.userId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val weeklyData by viewModel.weeklyData.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hydration History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Last 7 Days", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))

                        if (weeklyData.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No data yet. Start drinking! 💧",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        } else {
                            AndroidView(
                                factory = { ctx ->
                                    BarChart(ctx).apply {
                                        description.isEnabled = false
                                        legend.isEnabled = false
                                        setDrawGridBackground(false)
                                        setBackgroundColor(surfaceColor)
                                        setPinchZoom(false)
                                        isDoubleTapToZoomEnabled = false

                                        xAxis.apply {
                                            position = XAxis.XAxisPosition.BOTTOM
                                            granularity = 1f
                                            setDrawGridLines(false)
                                            textColor = onSurfaceColor
                                        }
                                        axisLeft.apply {
                                            textColor = onSurfaceColor
                                            setDrawGridLines(true)
                                            gridColor = AndroidColor.argb(30, 128, 128, 128)
                                        }
                                        axisRight.isEnabled = false
                                    }
                                },
                                update = { chart ->
                                    val labels = weeklyData.map { it.date.substring(5) }
                                    val entries = weeklyData.mapIndexed { i, d ->
                                        BarEntry(i.toFloat(), d.total.toFloat())
                                    }
                                    val dataSet = BarDataSet(entries, "ml").apply {
                                        color = primaryColor
                                        valueTextColor = onSurfaceColor
                                        valueTextSize = 10f
                                    }
                                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                                    chart.data = BarData(dataSet)
                                    chart.animateY(800)
                                    chart.invalidate()
                                },
                                modifier = Modifier.fillMaxWidth().height(220.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text("Daily Breakdown", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)
            }

            if (weeklyData.isEmpty()) {
                item {
                    Text("No entries logged yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            } else {
                items(weeklyData.reversed()) { day ->
                    DaySummaryRow(daily = day)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun DaySummaryRow(daily: DailyTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(daily.date, style = MaterialTheme.typography.titleMedium)
                Text("${daily.total} ml logged",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            val emoji = when {
                daily.total >= 2000 -> "🏆"
                daily.total >= 1500 -> "👍"
                daily.total >= 1000 -> "💧"
                else -> "😶‍🌫️"
            }
            Text(emoji, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
