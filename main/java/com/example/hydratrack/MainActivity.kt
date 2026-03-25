package com.example.hydratrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hydratrack.admin.AdminScreen
import com.example.hydratrack.data.UserProfile
import com.example.hydratrack.data.HydraRepository
import com.example.hydratrack.notifications.ReminderManager
import com.example.hydratrack.ui.screens.*
import com.example.hydratrack.ui.theme.HydraTrackTheme
import com.example.hydratrack.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferencesManager(this)
        val repo  = HydraRepository(this)

        setContent {
            var darkMode by remember { mutableStateOf(prefs.darkMode) }

            HydraTrackTheme(darkTheme = darkMode) {
                AppNavHost(
                    prefs         = prefs,
                    repo          = repo,
                    onThemeToggle = { darkMode = it }
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    prefs: PreferencesManager,
    repo: HydraRepository,
    onThemeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController  = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(
                isOnboardingDone = prefs.onboardingDone,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onComplete = { name, age, weightKg, goalMl, consentGiven ->
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.saveProfile(
                            UserProfile(
                                userId     = prefs.userId,
                                name       = name,
                                age        = age,
                                weightKg   = weightKg,
                                dailyGoalMl = goalMl
                            )
                        )
                    }
                    prefs.onboardingDone        = true
                    prefs.analyticsConsentGiven = consentGiven
                    ReminderManager.schedule(navController.context, prefs.reminderIntervalHours)

                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToHistory  = { navController.navigate("history") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("history") {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(
                onBack            = { navController.popBackStack() },
                onThemeToggle     = onThemeToggle,
                onNavigateToAdmin = { navController.navigate("admin") }
            )
        }

        composable("admin") {
            AdminScreen(onBack = { navController.popBackStack() })
        }
    }
}
