package com.icuxika.bittersweet.demo.util

import com.icuxika.bittersweet.demo.api.ProgressFlowState
import javafx.scene.control.ProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.Path

/**
 * 使用Flow来实现文件下载的逻辑
 */
object FileDownloader {
    suspend fun downloadFile(fileURL: String, filePath: Path): Flow<ProgressFlowState<Double>> {
        return flow<ProgressFlowState<Double>> {
            val byteBuffer = ByteBuffer.allocate(1024)
            runCatching {
                URI(fileURL).toURL()
                    .let { url: URL ->
                        val contentLength = getContentLength(url)
                        FileOutputStream(filePath.toFile()).use { fileOutputStream ->
                            Channels.newChannel(url.openStream()).use { channel ->
                                var bytesAllRead = 0
                                var bytesRead: Int
                                while (channel.read(byteBuffer).also { bytesRead = it } != -1) {
                                    byteBuffer.flip()
                                    val byteArray = ByteArray(bytesRead)
                                    byteBuffer.get(byteArray)
                                    fileOutputStream.write(byteArray)
                                    bytesAllRead += bytesRead
                                    if (contentLength == ProgressIndicator.INDETERMINATE_PROGRESS) {
                                        emit(ProgressFlowState.Success(contentLength))
                                    } else {
                                        emit(
                                            ProgressFlowState.Progress(
                                                (bytesAllRead.toDouble() / contentLength).coerceIn(
                                                    0.0,
                                                    1.0
                                                )
                                            )
                                        )
                                    }
                                    byteBuffer.clear()
                                }
                                fileOutputStream.flush()
                                emit(ProgressFlowState.Success(0.0))
                            }
                        }
                    }
            }.onFailure {
                emit(ProgressFlowState.Error(it))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获取文件大小
     */
    private fun getContentLength(url: URL): Double {
        var contentLength = ProgressIndicator.INDETERMINATE_PROGRESS
        runCatching {
            HttpURLConnection.setFollowRedirects(false)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            contentLength = connection.contentLength.toDouble()
            connection.disconnect()
        }
        return contentLength
    }

}