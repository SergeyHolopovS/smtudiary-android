package com.smtudiary.android.data

import kotlin.coroutines.cancellation.CancellationException

internal suspend inline fun <T> suspendRunCatching(
    crossinline block: suspend () -> T
): Result<T> = try {
    Result.success(block())
} catch (exception: CancellationException) {
    throw exception
} catch (exception: Exception) {
    Result.failure(exception)
}
