import com.icuxika.bittersweet.commons.net.request
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertTrue

class SerializationTest {

    @Test
    fun basic() {
        val response = request<ApiData<String>>("https://www.aprillie.com/go-transfer-station/getOne")
        println(response)
        assertTrue(response.code == 10000)

        val responseK = requestK<ApiData<String>>("https://www.aprillie.com/go-transfer-station/getOne")
        println(responseK)
        assertTrue(response.code == 10000)

        val user = User(1L, "icuxika")
        val apiData = ApiData<User>(data = user)

        val apiDataString = apiData.serialize()
        println(apiDataString)

        val apiDataObj = deserialize<ApiData<User>>(apiDataString)
        println(apiDataObj)
        assertTrue(apiDataObj.code == 10000)
    }
}

inline fun <reified T : Any> requestK(url: String): T {
    val serializer = serializer(typeOf<T>())
    return executeK(serializer, url)
}

fun <T> executeK(serializer: KSerializer<Any?>, url: String): T {
    val client = HttpClient.newBuilder().build();
    val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
    val responseString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
    @Suppress("UNCHECKED_CAST")
    return Json.decodeFromString(serializer, responseString) as T
}

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
data class User(var id: Long, var name: String)

inline fun <reified T : Any> T.serialize(): String {
    return Json.encodeToString(this)
}

inline fun <reified T : Any> deserialize(data: String): T {
    return Json.decodeFromString(data)
}
