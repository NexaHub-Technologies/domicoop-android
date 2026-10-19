package com.nexahub.domicoop.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.prefs by preferencesDataStore(name = "domicoop_prefs")

/**
 * Port of lib/session.ts.
 * Tokens live in EncryptedSharedPreferences (SecureStore); activity +
 * UI prefs live in DataStore (AsyncStorage). Idle TTL = 10 min, touch
 * throttled to 30s — same constants as the RN app.
 */
class SessionManager(private val appContext: Context) {

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val secure: SharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "domicoop_secure",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    suspend fun saveTokens(access: String, refresh: String) {
        secure.edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh)
            .putLong(KEY_ACTIVITY, System.currentTimeMillis())
            .apply()
    }

    suspend fun saveEmail(email: String) {
        appContext.prefs.edit { it[EMAIL] = email }
    }

    fun getEmailBlocking(): String? = runBlocking {
        appContext.prefs.data.map { it[EMAIL] }.first()
    }

    fun getTokenBlocking(): String? = secure.getString(KEY_ACCESS, null)

    suspend fun getToken(): String? = secure.getString(KEY_ACCESS, null)

    suspend fun getRefreshToken(): String? = secure.getString(KEY_REFRESH, null)

    /** Record activity (throttled 30s). */
    suspend fun touch() {
        val last = secure.getLong(KEY_ACTIVITY, 0)
        if (System.currentTimeMillis() - last < TOUCH_THROTTLE_MS) return
        secure.edit().putLong(KEY_ACTIVITY, System.currentTimeMillis()).apply()
    }

    suspend fun isValid(): Boolean {
        val token = secure.getString(KEY_ACCESS, null) ?: return false
        if (token.isBlank()) return false
        val last = secure.getLong(KEY_ACTIVITY, 0)
        if (last == 0L) return false
        return System.currentTimeMillis() - last < SESSION_TTL_MS
    }

    suspend fun isGuidelinesAcknowledged(): Boolean {
        return appContext.prefs.data.map { it[GUIDELINES_ACK] == true }.first()
    }

    suspend fun acknowledgeGuidelines() {
        appContext.prefs.edit { it[GUIDELINES_ACK] = true }
    }

    suspend fun clear() {
        secure.edit()
            .remove(KEY_ACCESS)
            .remove(KEY_REFRESH)
            .remove(KEY_ACTIVITY)
            .apply()
        appContext.prefs.edit {
            it.remove(EMAIL)
            it.remove(GUIDELINES_ACK)
        }
    }

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_ACTIVITY = "last_activity_at"
        private const val SESSION_TTL_MS = 10 * 60 * 1000L
        private const val TOUCH_THROTTLE_MS = 30 * 1000L
        private val EMAIL = stringPreferencesKey("user_email")
        private val GUIDELINES_ACK = booleanPreferencesKey("guidelines_acknowledged")
        private val THEME = stringPreferencesKey("theme_preference")
        private val GUIDELINES_SEEN = booleanPreferencesKey("has_seen_guidelines")
        private val LAST_ACTIVITY_FALLBACK = longPreferencesKey("last_activity_at")
    }

    // ---- UI prefs (ThemeContext + onboarding) ----
    suspend fun getTheme(): String = appContext.prefs.data.map { it[THEME] ?: "system" }.first()
    suspend fun setTheme(value: String) {
        appContext.prefs.edit { it[THEME] = value }
    }

    suspend fun hasSeenGuidelines(): Boolean =
        appContext.prefs.data.map { it[GUIDELINES_SEEN] == true }.first()

    suspend fun markGuidelinesSeen() {
        appContext.prefs.edit { it[GUIDELINES_SEEN] = true }
    }
}
