package com.icuxika.bittersweet.demo.util

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
    suspend fun downloadFile(fileURL: String, filePath: Path): Flow<DownloadState> {
        return flow {
            val byteBuffer = ByteBuffer.allocate(1024)
            runCatching {
                URI(fileURL).toURL()
                    .let { url: URL ->
                        val total = contentLength(url)
                        println("file size: $total")
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
                                    emit(
                                        DownloadState.Downloading(
                                            (bytesAllRead.toFloat() / total).coerceIn(
                                                0.0f,
                                                1.0f
                                            )
                                        )
                                    )
                                    byteBuffer.clear()
                                }
                                fileOutputStream.flush()
                                emit(DownloadState.Success(0))
                            }
                        }
                    }
            }.onFailure {
                emit(DownloadState.Error(it))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获取文件大小
     */
    private fun contentLength(url: URL): Int {
        var contentLength = -1
        runCatching {
            HttpURLConnection.setFollowRedirects(false)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            contentLength = connection.contentLength
            connection.disconnect()
        }
        return contentLength
    }

    /**
     * 下载状态
     */
    sealed class DownloadState {
        /**
         * 下载中
         */
        data class Downloading(val progress: Float) : DownloadState()

        /**
         * 下载成功
         */
        data class Success(val result: Int) : DownloadState()

        /**
         * 下载失败
         */
        data class Error(val throwable: Throwable) : DownloadState()
    }
}