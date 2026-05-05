package com.recompos.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.settingsDataStore by preferencesDataStore("recompos_settings")

data class AppPreferences(
    val onboardingComplete: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val unitSystem: String = "metric",
    val themeMode: String = "dark",
    val remindersEnabled: Boolean = true
)

class SettingsStore(private val context: Context) {
    private object Keys {
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val startDate = longPreferencesKey("start_date_epoch_day")
        val unitSystem = stringPreferencesKey("unit_system")
        val themeMode = stringPreferencesKey("theme_mode")
        val remindersEnabled = booleanPreferencesKey("reminders_enabled")
    }

    val preferences: Flow<AppPreferences> = context.settingsDataStore.data.map { prefs ->
        AppPreferences(
            onboardingComplete = prefs[Keys.onboardingComplete] ?: false,
            startDate = LocalDate.ofEpochDay(prefs[Keys.startDate] ?: LocalDate.now().toEpochDay()),
            unitSystem = prefs[Keys.unitSystem] ?: "metric",
            themeMode = prefs[Keys.themeMode] ?: "dark",
            remindersEnabled = prefs[Keys.remindersEnabled] ?: true
        )
    }

    suspend fun completeOnboarding(startDate: LocalDate, unitSystem: String) {
        context.settingsDataStore.edit {
            it[Keys.onboardingComplete] = true
            it[Keys.startDate] = startDate.toEpochDay()
            it[Keys.unitSystem] = unitSystem
            it[Keys.themeMode] = "dark"
            it[Keys.remindersEnabled] = true
        }
    }

    suspend fun updateTheme(themeMode: String) {
        context.settingsDataStore.edit { it[Keys.themeMode] = themeMode }
    }

    suspend fun updateUnits(unitSystem: String) {
        context.settingsDataStore.edit { it[Keys.unitSystem] = unitSystem }
    }

    suspend fun updateReminders(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.remindersEnabled] = enabled }
    }
}
