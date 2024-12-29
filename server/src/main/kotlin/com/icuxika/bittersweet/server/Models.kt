package com.icuxika.bittersweet.server

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ApiData<T>(
    @EncodeDefault
    var code: Int = 10000,
    @EncodeDefault
    var msg: String = "后端未返回",
    var data: T? = null
)

@Serializable
data class User(
    val id: Long,
    val username: String,
    @Serializable(with = JavaLocalDateSerializer::class)
    val localDate: LocalDate,
    @Serializable(with = JavaLocalTimeSerializer::class)
    val localTime: LocalTime,
    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val localDateTime: LocalDateTime
)
