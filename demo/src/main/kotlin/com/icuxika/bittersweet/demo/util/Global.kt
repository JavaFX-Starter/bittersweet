package com.icuxika.bittersweet.demo.util

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Global {

    private const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"
    private const val DEFAULT_TIME_PATTERN = "HH:mm:ss"
    private const val DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
    private val localDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)
    private val localTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)
    private val localDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
            override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                src?.let { return JsonPrimitive(it.format(localDateFormatter)) }
                    ?: throw IllegalStateException("无法解析LocalDate")
            }

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDate {
                json?.asJsonPrimitive?.asString?.let {
                    return LocalDate.parse(it, localDateFormatter)
                } ?: throw IllegalStateException("无法解析LocalDate")
            }
        })
        .registerTypeAdapter(LocalTime::class.java, object : JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
            override fun serialize(src: LocalTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                src?.let { return JsonPrimitive(it.format(localTimeFormatter)) }
                    ?: throw IllegalStateException("无法解析LocalTime")
            }

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalTime {
                json?.asJsonPrimitive?.asString?.let {
                    return LocalTime.parse(it, localTimeFormatter)
                } ?: throw IllegalStateException("无法解析LocalTime")
            }
        })
        .registerTypeAdapter(
            LocalDateTime::class.java,
            object : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
                override fun serialize(
                    src: LocalDateTime?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement {
                    src?.let { return JsonPrimitive(it.format(localDateTimeFormatter)) }
                        ?: throw IllegalStateException("无法解析LocalDateTime")
                }

                override fun deserialize(
                    json: JsonElement?,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): LocalDateTime {
                    json?.asJsonPrimitive?.asString?.let {
                        return LocalDateTime.parse(it, localDateTimeFormatter)
                    } ?: throw IllegalStateException("无法解析LocalDateTime")
                }
            })
        .create()
}