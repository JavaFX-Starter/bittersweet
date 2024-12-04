import com.icuxika.bittersweet.commons.net.request
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SerializationTest {

    @Test
    fun basic() {
        val response = request<ApiData<String>>("https://www.aprillie.com/go-transfer-station/getOne")
        println(response)

        val user = User(1L, "icuxika")
        val apiData = ApiData<User>(data = user)

        val apiDataString = apiData.serialize()
        println(apiDataString)

        val apiDataObj = deserialize<ApiData<User>>(apiDataString)
        println(apiDataObj)
    }
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
