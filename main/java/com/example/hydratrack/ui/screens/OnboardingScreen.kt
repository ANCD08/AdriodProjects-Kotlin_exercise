package com.example.hydratrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onComplete: (
        name: String,
        age: Int,
        weightKg: Float,
        goalMl: Int,
        consentGiven: Boolean
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }

    var name         by remember { mutableStateOf("") }
    var age          by remember { mutableStateOf("") }
    var weight       by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableIntStateOf(2_000) }
    var consent      by remember { mutableStateOf(false) }

    val stepTitles = listOf("Welcome", "Profile", "Goal", "Privacy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stepTitles.indices.forEach { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (i <= step) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                slideOutHorizontally { -it } + fadeOut()
            },
            label = "onboarding_step"
        ) { currentStep ->
            when (currentStep) {
                0 -> WelcomeStep(onNext = { step++ })
                1 -> ProfileStep(
                    name = name, onNameChange = { name = it },
                    age  = age,  onAgeChange  = { age = it },
                    weight = weight, onWeightChange = { weight = it },
                    onNext = { step++ }, onBack = { step-- }
                )
                2 -> GoalStep(
                    selectedGoal    = selectedGoal,
                    onGoalSelected  = { selectedGoal = it },
                    onNext = { step++ }, onBack = { step-- }
                )
                3 -> PrivacyStep(
                    consentGiven    = consent,
                    onConsentChange = { consent = it },
                    onComplete = {
                        onComplete(
                            name,
                            age.toIntOrNull() ?: 25,
                            weight.toFloatOrNull() ?: 70f,
                            selectedGoal,
                            consent
                        )
                    },
                    onBack = { step-- }
                )
            }
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("💧", style = MaterialTheme.typography.displayLarge)
        Text(
            text      = "Welcome to HydraTrack",
            style     = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text      = "Stay hydrated effortlessly.\nJust shake your phone after every glass of water and we handle the rest.",
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick  = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(16.dp)
        ) {
            Text("Get Started", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ProfileStep(
    name: String, onNameChange: (String) -> Unit,
    age: String,  onAgeChange:  (String) -> Unit,
    weight: String, onWeightChange: (String) -> Unit,
    onNext: () -> Unit, onBack: () -> Unit
) {
    val isValid = name.isNotBlank() && age.isNotBlank() && weight.isNotBlank()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Tell us about you",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = name, onValueChange = onNameChange,
            label = { Text("First Name") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = age, onValueChange = onAgeChange,
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = weight, onValueChange = onWeightChange,
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        NavButtons(onBack = onBack, onNext = onNext, nextEnabled = isValid)
    }
}

@Composable
private fun GoalStep(
    selectedGoal: Int, onGoalSelected: (Int) -> Unit,
    onNext: () -> Unit, onBack: () -> Unit
) {
    val goals = listOf(1_500, 2_000, 2_500, 3_000)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Set your daily goal",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Recommended: 2,000–2,500 ml for most adults",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
        )
        goals.forEach { goal ->
            val selected = goal == selectedGoal
            Card(
                onClick = { onGoalSelected(goal) },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                                     else          MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$goal ml / day",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                                else          MaterialTheme.colorScheme.onSurface
                    )
                    if (selected) Text("✓", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        NavButtons(onBack = onBack, onNext = onNext)
    }
}

@Composable
private fun PrivacyStep(
    consentGiven: Boolean, onConsentChange: (Boolean) -> Unit,
    onComplete: () -> Unit, onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "🔒 Your Privacy",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("How we handle your data:", style = MaterialTheme.typography.titleMedium)
                listOf(
                    "✅  All data is stored ONLY on this device",
                    "✅  Nothing is ever sent to external servers",
                    "✅  No internet permission is requested",
                    "✅  You can delete all data from Settings at any time",
                    "ℹ️   Optional: allow the local admin panel to include your anonymised stats"
                ).forEach {
                    Text(it, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }

        Card(
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (consentGiven) MaterialTheme.colorScheme.primaryContainer
                                 else              MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier  = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Allow usage analytics", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Opt-in — off by default. Lets the developer view your anonymised hydration stats via the local admin panel only. You can change this anytime in Settings → Privacy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Switch(checked = consentGiven, onCheckedChange = onConsentChange)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick  = onBack,
                modifier = Modifier.weight(1f).height(52.dp),
                shape    = RoundedCornerShape(16.dp)
            ) { Text("Back") }
            Button(
                onClick  = onComplete,
                modifier = Modifier.weight(2f).height(52.dp),
                shape    = RoundedCornerShape(16.dp)
            ) { Text("Start Tracking! 💧") }
        }
    }
}

@Composable
private fun NavButtons(
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean = true
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick  = onBack,
            modifier = Modifier.weight(1f).height(52.dp),
            shape    = RoundedCornerShape(16.dp)
        ) { Text("Back") }
        Button(
            onClick  = onNext,
            modifier = Modifier.weight(2f).height(52.dp),
            shape    = RoundedCornerShape(16.dp),
            enabled  = nextEnabled
        ) { Text("Next") }
    }
}
