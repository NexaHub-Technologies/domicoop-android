package com.nexahub.domicoop.data.remote

import com.nexahub.domicoop.BuildConfig
import com.nexahub.domicoop.core.ApiException
import com.nexahub.domicoop.data.local.SessionManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val DomiJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

/**
 * Mirrors lib/http.ts: 20s timeout, Bearer injection, single-flight 401
 * refresh-and-retry, ApiException(status, message).
 */
class AuthInterceptor(private val session: SessionManager) : Interceptor {
    private val refreshMutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = session.getTokenBlocking()
        var request = attachToken(chain.request(), token)
        var response = chain.proceed(request)
        if (response.code != 401) return response
        response.close()

        val refreshed = runCatching {
            kotlinx.coroutines.runBlocking {
                refreshMutex.withLock { tryRefresh() }
            }
        }.getOrDefault(false)
        if (!refreshed) {
            // Surface 401 to the repository layer; it clears the session.
            throw ApiException(401, "Not authenticated")
        }
        val newToken = session.getTokenBlocking()
        request = attachToken(chain.request(), newToken)
        return chain.proceed(request)
    }

    private fun attachToken(request: Request, token: String?): Request {
        if (token.isNullOrBlank()) return request
        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }

    private suspend fun tryRefresh(): Boolean {
        // Check token again inside the lock (single-flight).
        val refreshToken = session.getRefreshToken() ?: return false
        return try {
            val plain = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL.trimEnd('/') + "/")
                .client(plain)
                .addConverterFactory(DomiJson.asConverterFactory("application/json".toMediaType()))
                .build()
            val api = retrofit.create(ApiService::class.java)
            val res = api.refresh(mapOf("refresh_token" to refreshToken))
            session.saveTokens(res.accessToken, res.refreshToken)
            true
        } catch (_: Exception) {
            false
        }
    }
}

fun provideOkHttp(session: SessionManager): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    return OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(session))
        .addInterceptor(logging)
        .retryOnConnectionFailure(true)
        .build()
}

fun provideApi(client: OkHttpClient): ApiService {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL.trimEnd('/') + "/")
        .client(client)
        .addConverterFactory(DomiJson.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)
}

/** Maps Retrofit/IO failures to ApiException like lib/http.ts. */
suspend fun <T> safeCall(block: suspend () -> T): T {
    try {
        return block()
    } catch (e: ApiException) {
        throw e
    } catch (e: HttpException) {
        val body = e.response()?.errorBody()?.string()
        val msg = body?.take(300) ?: "Request failed: ${e.code()}"
        // Try to extract {message|error} from JSON body.
        val friendly = try {
            val el = DomiJson.parseToJsonElement(body ?: "")
            val obj = el as? kotlinx.serialization.json.JsonObject
            obj?.get("message")?.toString()?.trim('"')
                ?: obj?.get("error")?.toString()?.trim('"')
                ?: msg
        } catch (_: Exception) {
            msg
        }
        throw ApiException(e.code(), friendly, body)
    } catch (e: java.net.SocketTimeoutException) {
        throw ApiException(0, "Request timed out. Check your connection and try again.")
    } catch (e: java.io.IOException) {
        throw ApiException(0, "Network request failed. Check your connection and try again.")
    }
}
