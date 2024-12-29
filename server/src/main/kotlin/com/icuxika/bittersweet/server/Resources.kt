package com.icuxika.bittersweet.server

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Resource("/users")
data class Users(
    val id: Long? = null,
    val username: String? = null,
    @Serializable(with = JavaLocalDateSerializer::class)
    val localDate: LocalDate? = null,
    @Serializable(with = JavaLocalTimeSerializer::class)
    val localTime: LocalTime? = null,
    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val localDateTime: LocalDateTime? = null,
) {
    @Resource("upload")
    data class Upload(val parent: Users = Users())

    @Resource("download")
    data class Download(val parent: Users = Users())
}
