package turfbeat.com.data.remote

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import turfbeat.com.util.Constants

private val Context.tokenDataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

class TokenManager(
    private val context: Context
) {
    private val accessTokenKey = stringPreferencesKey(Constants.KEY_ACCESS_TOKEN)
    private val refreshTokenKey = stringPreferencesKey(Constants.KEY_REFRESH_TOKEN)
    private val userIdKey = stringPreferencesKey(Constants.KEY_USER_ID)
    private val userRoleKey = stringPreferencesKey(Constants.KEY_USER_ROLE)
    private val isLoggedInKey = stringPreferencesKey(Constants.KEY_IS_LOGGED_IN)

    val accessToken: Flow<String?> = context.tokenDataStore.data.map { it[accessTokenKey] }

    val isLoggedIn: Flow<Boolean> = context.tokenDataStore.data.map {
        it[isLoggedInKey].let { value ->
            when (value) {
                "true" -> true
                "false" -> false
                else -> false
            }
        }
    }

    val userId: Flow<Int?> = context.tokenDataStore.data.map {
        it[userIdKey]?.toIntOrNull()
    }

    val userRole: Flow<String?> = context.tokenDataStore.data.map { it[userRoleKey] }

    suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        context.tokenDataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            if (refreshToken != null) prefs[refreshTokenKey] = refreshToken
            prefs[isLoggedInKey] = "true"
        }
    }

    suspend fun saveUserInfo(userId: Int, role: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[userIdKey] = userId.toString()
            prefs[userRoleKey] = role
        }
    }

    suspend fun getAccessToken(): String? {
        return context.tokenDataStore.data.first()[accessTokenKey]
    }

    suspend fun getRefreshToken(): String? {
        return context.tokenDataStore.data.first()[refreshTokenKey]
    }

    suspend fun getUserId(): Int? {
        return context.tokenDataStore.data.first()[userIdKey]?.toIntOrNull()
    }

    suspend fun clearSession() {
        context.tokenDataStore.edit { it.clear() }
    }
}
