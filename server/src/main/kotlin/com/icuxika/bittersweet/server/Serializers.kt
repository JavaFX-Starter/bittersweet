package com.icuxika.bittersweet.server

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"
private const val DEFAULT_TIME_PATTERN = "HH:mm:ss"
private const val DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class JavaLocalDateSerializer : KSerializer<LocalDate> {
    private val localDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(localDateFormatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), localDateFormatter)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalTime::class)
class JavaLocalTimeSerializer : KSerializer<LocalTime> {
    private val localTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)
    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.format(localTimeFormatter))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString(), localTimeFormatter)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
class JavaLocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val localDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(localDateTimeFormatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), localDateTimeFormatter)
    }
}
