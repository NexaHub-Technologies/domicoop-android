package com.nexahub.domicoop.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.cacheStore by preferencesDataStore(name = "domicoop_cache")

/**
 * Port of usePersistedQuery's AsyncStorage write-through cache:
 * last-good JSON per key, served on network error (except 403).
 */
class JsonCache(private val appContext: Context) {
    suspend fun put(key: String, json: String) {
        appContext.cacheStore.edit { it[stringPreferencesKey(key)] = json }
    }

    suspend fun get(key: String): String? {
        return appContext.cacheStore.data.map { it[stringPreferencesKey(key)] }.first()
    }

    suspend fun clearKeys(vararg keys: String) {
        appContext.cacheStore.edit {
            keys.forEach { k -> it.remove(stringPreferencesKey(k)) }
        }
    }

    suspend fun clearAll() {
        appContext.cacheStore.edit { it.clear() }
    }

    companion object {
        const val CONTRIBUTIONS = "cached_contributions_response"
        const val LOANS = "cached_loans_response"
        const val DASHBOARD = "cached_savings_summary"
        const val NOTIFICATIONS_P1 = "cached_notifications_page1"
        const val PROFILE = "cached_profile"
    }
}
