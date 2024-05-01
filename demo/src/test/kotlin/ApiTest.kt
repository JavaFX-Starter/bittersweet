import com.icuxika.bittersweet.demo.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.runTest
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiTest {

    data class User(
        val id: Long,
        val username: String,
        val localDate: LocalDate,
        val localTime: LocalTime,
        val localDateTime: LocalDateTime
    )

    @Test
    fun getTest() = runTest {
        // ApiData<User> getTest(@RequestParam("id") Long id, @RequestParam("username") String username, @RequestParam("localDate") LocalDate localDate, @RequestParam("localTime") LocalTime localTime, @RequestParam("localDateTime") LocalDateTime localDateTime)
        val apiData =
            suspendGet<ApiData<User>>("http://127.0.0.1:8080/test/getTest?id=1&username=icuxika&localDate=2024-05-01&localTime=16:58:00&localDateTime=2024-05-01 16:58:00")
        println(apiData)
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun postJsonTest() = runTest {
        // ApiData<User> postJsonTest(@RequestBody User user)
        val apiData = suspendPost<ApiData<User>>(
            "http://127.0.0.1:8080/test/postJsonTest",
            User(1, "icuxika", LocalDate.of(2024, 5, 20), LocalTime.of(17, 12), LocalDateTime.of(2024, 5, 20, 17, 12))
        )
        println(apiData)
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun postFileTest() = runTest {
        // ApiData<User> postFileTest(@RequestPart("file") MultipartFile file, @RequestParam("id") Long id)
        val apiData = suspendPost<ApiData<Unit>>(
            "http://127.0.0.1:8080/test/postFileTest", mapOf(
                Api.REQUEST_KEY_FILE to File("/Users/icuxika/IdeaProjects/bittersweet/demo/src/main/kotlin/com/icuxika/bittersweet/demo/api/ApiData.kt"),
                "id" to "11"
            )
        )
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun postFileListTest() = runTest {
        // ApiData<User> postFileListTest(@RequestPart("fileList") List<MultipartFile> fileList, @RequestParam("id") Long id, @RequestParam("time") LocalDateTime time)
        val apiData = suspendPost<ApiData<Unit>>(
            "http://127.0.0.1:8080/test/postFileListTest", mapOf(
                Api.REQUEST_KEY_FILE_LIST to listOf(
                    File("/Users/icuxika/IdeaProjects/bittersweet/demo/src/main/kotlin/com/icuxika/bittersweet/demo/api/ApiData.kt"),
                    File("/Users/icuxika/IdeaProjects/bittersweet/demo/Dataset/AliYunDataV/aliyun_datav_100000_cn/aliyun_datav_100000_cn.shp")
                ),
                "id" to "11",
                "time" to "2024-05-01 16:58:00"
            )
        )
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun uploadFileTest() = runTest {
        suspendPostFileFlow<ApiData<User>>(
            "http://127.0.0.1:8080/test/postFileTest", mapOf(
                Api.REQUEST_KEY_FILE to File("/Users/icuxika/IdeaProjects/bittersweet/demo/src/main/resources/com/icuxika/bittersweet/demo/fonts/HarmonyOS-Sans/HarmonyOS_Sans_Black.ttf"),
                "id" to "11",
            )
        ).flowOn(Dispatchers.IO).collect {
            when (it) {
                is ProgressFlowState.Progress -> {
                    println(it.progress)
                }

                is ProgressFlowState.Error -> {
                    it.throwable.printStackTrace()
                }

                is ProgressFlowState.Success -> {
                    (it.result as? ApiData<*>)?.let { apiData ->
                        println(apiData)
                    }
                }
            }
        }
    }

    @Test
    fun downloadFileTest() = runTest {
        suspendGetFileFlow(
            "http://127.0.0.1:8080/test/downloadFileTest",
            Path.of("/Users/icuxika/Downloads/test.mp4")
        ).flowOn(Dispatchers.IO).collect {
            when (it) {
                is ProgressFlowState.Progress -> {
                    println(it.progress)
                }

                is ProgressFlowState.Error -> {
                    it.throwable.printStackTrace()
                }

                is ProgressFlowState.Success -> {
                    println(it.result)
                }
            }
        }
    }
}