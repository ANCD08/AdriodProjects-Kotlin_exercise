package com.example.hydratrack.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydratrack.data.HydraRepository
import com.example.hydratrack.data.LogMethod
import com.example.hydratrack.utils.PreferencesManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val currentIntakeMl: Int = 0,
    val dailyGoalMl: Int = 2000,
    val servingMl: Int = 250,
    val isGoalMet: Boolean = false,
    val canUndo: Boolean = false,
    val undoCountdownSec: Int = 0
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = HydraRepository(app)
    private val prefs = PreferencesManager(app)

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var undoJob: Job? = null

    init {
        viewModelScope.launch {
            val profile = repo.getProfile(prefs.userId)
            _state.update { it.copy(
                userName = profile?.name ?: "",
                dailyGoalMl = profile?.dailyGoalMl ?: 2000,
                servingMl = prefs.servingMl
            ) }

            repo.getDailyIntakeFlow(prefs.userId).collect { intake ->
                _state.update { it.copy(
                    currentIntakeMl = intake,
                    isGoalMet = intake >= it.dailyGoalMl
                ) }
            }
        }
    }

    fun refreshServing() {
        _state.update { it.copy(servingMl = prefs.servingMl) }
    }

    fun logWater(method: LogMethod) {
        viewModelScope.launch {
            repo.logWater(prefs.userId, state.value.servingMl, method)
            startUndoCountdown()
        }
    }

    fun undoLastLog() {
        undoJob?.cancel()
        viewModelScope.launch {
            repo.undoLastLog(prefs.userId)
            _state.update { it.copy(canUndo = false) }
        }
    }

    private fun startUndoCountdown() {
        undoJob?.cancel()
        undoJob = viewModelScope.launch {
            _state.update { it.copy(canUndo = true, undoCountdownSec = 10) }
            for (i in 9 downTo 0) {
                delay(1000)
                _state.update { it.copy(undoCountdownSec = i) }
            }
            _state.update { it.copy(canUndo = false) }
        }
    }
}
