package com.icuxika.bittersweet.demo.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class User(
    val id: Long,
    val username: String,
    val localDate: LocalDate,
    val localTime: LocalTime,
    val localDateTime: LocalDateTime
)
