package com.icuxika.bittersweet.server

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test

class MainAppTest {

    /**
     * https://github.com/ktorio/ktor-documentation/blob/3.0.2/codeSnippets/snippets/resource-routing/src/test/kotlin/resourcerouting/ApplicationTest.kt
     */
    @Test
    fun testGetAllUsers() = testApplication {
        application {
            module()
        }
        val response =
            client.get("/users?id=1&username=icuxika&localDate=2024-05-01&localTime=16:58:00&localDateTime=2024-05-01 16:58:00")
        println(response.bodyAsText())
    }

    @Test
    fun testPostUser() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(User(1, "icuxika", LocalDate.now(), LocalTime.now(), LocalDateTime.now()))
        }
        println(response.bodyAsText())
    }

    /**
     * https://github.com/ktorio/ktor-documentation/blob/3.0.2/codeSnippets/snippets/upload-file/src/test/kotlin/uploadfile/UploadFileTest.kt
     */
    @Test
    fun testPostUserUpload() = testApplication {
        application {
            module()
        }
        val file1 = File("build.gradle.kts")
        val file2 = File("src/main/resources/application.yaml")
        val response = client.post("/users/upload") {
            setBody(MultiPartFormDataContent(formData {
                append("id", 1L)
                append("description", "Build Scripts")
                append("file", file1.readBytes(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=${file1.name}")
                })
                append("file", file2.readBytes(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=${file2.name}")
                })
            }))
        }
        println(response.bodyAsText())
    }

    /**
     * https://github.com/ktorio/ktor-documentation/blob/3.0.2/codeSnippets/snippets/client-download-streaming/src/main/kotlin/com/example/Application.kt
     */
    @Test
    fun testGetUserDownload() = testApplication {
        application {
            module()
        }
        client.prepareGet("/users/download").execute { httpResponse ->
            val fileName = ContentDisposition.parse(httpResponse.headers[HttpHeaders.ContentDisposition] ?: "")
                .parameter(ContentDisposition.Parameters.FileName)
            println("fileName: $fileName")
            val channel: ByteReadChannel = httpResponse.body()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.exhausted()) {
                    val bytes = packet.readByteArray()
                    println(
                        "Received ${bytes.size} bytes from ${httpResponse.contentLength()} ->\n${
                            bytes.decodeToString(
                                0,
                                0 + bytes.size
                            )
                        }"
                    )
                }
            }
        }
    }
}