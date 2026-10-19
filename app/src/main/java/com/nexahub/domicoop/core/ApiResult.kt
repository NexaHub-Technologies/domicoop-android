package com.nexahub.domicoop.core

/** Mirrors lib/http.ts ApiError. status=0 → network/timeout. Extends IOException so OkHttp interceptors can throw it. */
class ApiException(
    val status: Int,
    override val message: String,
    val body: String? = null,
) : java.io.IOException(message)

sealed interface ApiResult<out T> {
    data class Ok<T>(val value: T) : ApiResult<T>
    data class Err(val error: ApiException) : ApiResult<Nothing>
}

fun <T> ApiResult<T>.getOrNull(): T? = (this as? ApiResult.Ok)?.value
fun <T> ApiResult<T>.errorOrNull(): ApiException? = (this as? ApiResult.Err)?.error
