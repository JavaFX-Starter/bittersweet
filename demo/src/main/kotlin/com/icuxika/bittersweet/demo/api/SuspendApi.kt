package com.icuxika.bittersweet.demo.api

import com.google.gson.reflect.TypeToken
import com.icuxika.bittersweet.demo.api.Api.HTTPRequestMethod
import javafx.scene.control.ProgressIndicator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.use
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Get请求，最好设置为不允许通过请求体传参
 */
suspend inline fun <reified T> suspendGet(
    url: String,
    data: Any? = null,
) = suspendCancellableCoroutine { cancellableContinuation ->
    val type = object : TypeToken<T>() {}.type
    Api.request<T>(type, url, HTTPRequestMethod.GET, data)
        .success {
            cancellableContinuation.resume(it)
        }.failure {
            cancellableContinuation.resumeWithException(it)
        }.execute()
}

/**
 * Post请求
 */
suspend inline fun <reified T> suspendPost(
    url: String,
    data: Any? = null,
) = suspendCancellableCoroutine { cancellableContinuation ->
    val type = object : TypeToken<T>() {}.type
    Api.request<T>(type, url, HTTPRequestMethod.POST, data)
        .success {
            cancellableContinuation.resume(it)
        }.failure {
            cancellableContinuation.resumeWithException(it)
        }.execute()
}

sealed class ProgressFlowState<T> {
    data class Progress<T>(val progress: Double) : ProgressFlowState<T>()
    data class Success<T>(val result: T? = null) : ProgressFlowState<T>()
    data class Error<T>(val throwable: Throwable) : ProgressFlowState<T>()
}

/**
 * 获取文件流并保存到指定Path
 */
suspend fun suspendGetFileFlow(
    url: String,
    filePath: Path,
    data: Any? = null,
) = callbackFlow<ProgressFlowState<Int>> {
    val result = runCatching {
        suspendCancellableCoroutine { cancellableContinuation ->
            val type = object : TypeToken<Pair<InputStream, Double>>() {}.type
            Api.request<Pair<InputStream, Double>>(type, url, HTTPRequestMethod.GET, data)
                .success {
                    cancellableContinuation.resume(it)
                }.failure {
                    cancellableContinuation.resumeWithException(it)
                }.stream()
        }
    }
    result.getOrNull()?.let { pair ->
        val (inputStream, contentLength) = pair
        BufferedInputStream(inputStream).use { bufferedInputStream ->
            FileOutputStream(filePath.toFile()).use { fileOutputStream ->
                val buffer = ByteArray(1024)
                var bytesAllRead = 0
                var bytesRead: Int
                while (bufferedInputStream.read(buffer).also { bytesRead = it } != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead)
                    bytesAllRead += bytesRead
                    if (contentLength == ProgressIndicator.INDETERMINATE_PROGRESS) {
                        trySend(ProgressFlowState.Progress(contentLength))
                    } else {
                        trySend(
                            ProgressFlowState.Progress(
                                (bytesAllRead.toDouble() / contentLength).coerceIn(0.0, 1.0)
                            )
                        )
                    }
                }
                fileOutputStream.flush()
                send(ProgressFlowState.Success(0))
            }
        }
    }
    result.exceptionOrNull()?.let {
        send(ProgressFlowState.Error(it))
    }
    close()
    awaitClose { }
}

/**
 * 上传文件并同时监听进度，监听器会在请求真正发起之前就被调用
 */
suspend inline fun <reified T> suspendPostFileFlow(
    url: String,
    data: Any? = null,
) = callbackFlow<ProgressFlowState<T>> {
    val result = runCatching {
        suspendCancellableCoroutine { cancellableContinuation ->
            val type = object : TypeToken<T>() {}.type
            Api.request<T>(type, url, HTTPRequestMethod.POST, mutableMapOf<String, Any>(
                Api.REQUEST_KEY_LISTENER to object : RequestListener {
                    override fun invoke(workDone: Long, max: Long) {
                        trySend(ProgressFlowState.Progress(workDone.toDouble() / max))
                    }
                }
            ).apply {
                @Suppress("UNCHECKED_CAST")
                (data as? Map<String, Any>)?.forEach { (k, v) ->
                    this[k] = v
                }
            })
                .success {
                    cancellableContinuation.resume(it)
                }.failure {
                    cancellableContinuation.resumeWithException(it)
                }.execute()
        }
    }
    result.getOrNull()?.let {
        send(ProgressFlowState.Success(it))
    }
    result.exceptionOrNull()?.let {
        send(ProgressFlowState.Error(it))
    }
    close()
    awaitClose { }
}