package com.icuxika.bittersweet.server

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun Application.module() {
    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    install(ContentNegotiation) {
        json()
    }
    install(Resources)
    // https://github.com/ktorio/ktor-documentation/blob/3.0.2/codeSnippets/snippets/resource-routing/src/main/kotlin/resourcerouting/Application.kt
    routing {
        get("/") {
            call.respondText("Hello World! Listening on port $port")
        }
        get<Users> { users ->
            println("get -> $users")
            val user = User(
                users.id ?: 0L,
                users.username ?: "icuxika",
                users.localDate ?: LocalDate.now(),
                users.localTime ?: LocalTime.now(),
                users.localDateTime ?: LocalDateTime.now()
            )
            call.respond(ApiData(data = user))
        }
        post<Users> {
            val user = call.receive<User>()
            println("post -> $user")
            call.respond(ApiData(data = user))
        }
        post<Users.Upload> {
            val receivedFiles = arrayListOf<Pair<String, Int>>()
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                val fileName: String
                val fileSize: Int

                when (part) {
                    is PartData.FormItem -> {
                        println("${part.name} -> ${part.value}")
                    }

                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.provider().readRemaining().readByteArray()
                        fileSize = fileBytes.size

                        println("receive file: $fileName, size: $fileSize")
                        receivedFiles.add(Pair(fileName, fileSize))
                    }

                    else -> {}
                }
                part.dispose()
            }
            call.respond(ApiData(data = null, msg = "receive files: $receivedFiles"))
        }
        get<Users.Download> {
            val file = File("build.gradle.kts")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, file.name)
                    .toString()
            )
            call.respondFile(file)
        }
    }
}
