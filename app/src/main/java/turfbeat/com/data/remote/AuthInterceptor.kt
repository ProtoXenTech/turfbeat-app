package turfbeat.com.data.remote

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import turfbeat.com.util.Constants

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            context.dataStore.data.map { prefs ->
                prefs[stringPreferencesKey(Constants.KEY_ACCESS_TOKEN)]
            }.first()
        }

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader(Constants.HEADER_AUTHORIZATION, "${Constants.AUTH_TOKEN_TYPE} $token")
                .addHeader(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build()
        } else {
            chain.request().newBuilder()
                .addHeader(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build()
        }

        return chain.proceed(request)
    }
}
