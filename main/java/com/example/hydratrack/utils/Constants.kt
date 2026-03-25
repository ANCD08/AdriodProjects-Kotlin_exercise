package com.example.hydratrack.utils

object Constants {

    // ── Shake Detection ──────────────────────────────────────────────────────
    const val SHAKE_THRESHOLD            = 12.0f   // m/s² above gravity
    const val SHAKE_COOLDOWN_MS          = 2_000L  // 2 s between valid shakes
    const val SHAKE_CONFIRMATION_WINDOW_MS = 500L  // Must stay above threshold for 500 ms
    const val SHAKE_MIN_DIRECTION_CHANGES = 2      // Must oscillate (not a single bump)

    // ── Serving Sizes (ml) ───────────────────────────────────────────────────
    val SERVING_OPTIONS     = listOf(150, 200, 250, 330, 500, 750)
    const val DEFAULT_SERVING_ML   = 250
    const val DEFAULT_DAILY_GOAL_ML = 2_000

    // ── Undo Window ──────────────────────────────────────────────────────────
    const val UNDO_WINDOW_MS = 10_000L  // 10 s to undo a logged entry

    // ── Notifications ────────────────────────────────────────────────────────
    const val NOTIFICATION_CHANNEL_ID   = "hydratrack_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Hydration Reminders"
    const val NOTIFICATION_ID           = 1001
    const val WORK_TAG_REMINDER         = "hydra_reminder"

    // ── Database ─────────────────────────────────────────────────────────────
    const val DB_NAME    = "hydratrack.db"
    const val DB_VERSION = 1

    // ── SharedPreferences ─────────────────────────────────────────────────────
    const val PREFS_NAME               = "hydratrack_prefs"
    const val PREF_ONBOARDING_DONE     = "onboarding_done"
    const val PREF_DARK_MODE           = "dark_mode"
    const val PREF_SERVING_ML          = "serving_ml"
    const val PREF_REMINDER_INTERVAL_H = "reminder_interval_hours"
    const val PREF_CONSENT_GIVEN       = "analytics_consent"    // opt-IN, default false
    const val PREF_USER_ID             = "user_id"
    const val PREF_SHAKE_SENSITIVITY   = "shake_sensitivity"

    // ── Admin ─────────────────────────────────────────────────────────────────
    // In a real release, replace with a hashed credential stored securely.
    const val ADMIN_PIN = "hydra2026"

    // ── Export ────────────────────────────────────────────────────────────────
    const val EXPORT_FILE_PREFIX = "hydratrack_export_"
}
