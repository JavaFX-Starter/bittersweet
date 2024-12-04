package com.icuxika.bittersweet.commons.net

import com.icuxika.bittersweet.commons.serialization.typeTokenOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

inline fun <reified T : Any> request(url: String): T {
    val serializer = serializer(typeTokenOf<T>())
    return execute(serializer, url)
}

fun <T> execute(serializer: KSerializer<Any>, url: String): T {
    val client = HttpClient.newBuilder().build();
    val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
    val responseString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
    @Suppress("UNCHECKED_CAST")
    return Json.decodeFromString(serializer, responseString) as T
}
