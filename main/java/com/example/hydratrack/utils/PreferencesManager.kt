package com.example.hydratrack.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    var onboardingDone: Boolean
        get() = prefs.getBoolean(Constants.PREF_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(Constants.PREF_ONBOARDING_DONE, value).apply()

    var darkMode: Boolean
        get() = prefs.getBoolean(Constants.PREF_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(Constants.PREF_DARK_MODE, value).apply()

    var servingMl: Int
        get() = prefs.getInt(Constants.PREF_SERVING_ML, Constants.DEFAULT_SERVING_ML)
        set(value) = prefs.edit().putInt(Constants.PREF_SERVING_ML, value).apply()

    var reminderIntervalHours: Int
        get() = prefs.getInt(Constants.PREF_REMINDER_INTERVAL_H, 2)
        set(value) = prefs.edit().putInt(Constants.PREF_REMINDER_INTERVAL_H, value).apply()

    var analyticsConsentGiven: Boolean
        get() = prefs.getBoolean(Constants.PREF_CONSENT_GIVEN, false)
        set(value) = prefs.edit().putBoolean(Constants.PREF_CONSENT_GIVEN, value).apply()

    val userId: String
        get() {
            var id = prefs.getString(Constants.PREF_USER_ID, null)
            if (id == null) {
                id = UUID.randomUUID().toString()
                prefs.edit().putString(Constants.PREF_USER_ID, id).apply()
            }
            return id
        }

    var shakeSensitivity: Float
        get() = prefs.getFloat(Constants.PREF_SHAKE_SENSITIVITY, Constants.SHAKE_THRESHOLD)
        set(value) = prefs.edit().putFloat(Constants.PREF_SHAKE_SENSITIVITY, value).apply()
}
