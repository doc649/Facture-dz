package com.example.facturedz.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define DataStore instance at the top level
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SUSPICIOUS_AMOUNT_THRESHOLD = doublePreferencesKey("suspicious_amount_threshold")
    }

    val suspiciousAmountThreshold: Flow<Double> = context.dataStore.data
        .map {
            preferences ->
            preferences[PreferencesKeys.SUSPICIOUS_AMOUNT_THRESHOLD] ?: 1000000.0 // Default value
        }

    suspend fun updateSuspiciousAmountThreshold(threshold: Double) {
        context.dataStore.edit {
            preferences ->
            preferences[PreferencesKeys.SUSPICIOUS_AMOUNT_THRESHOLD] = threshold
        }
    }
}
