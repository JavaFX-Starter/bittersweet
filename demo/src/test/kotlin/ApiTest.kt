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

/**
 * .\gradlew.bat :server:run -Pdevelopment
 */
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
            suspendGet<ApiData<User>>("http://127.0.0.1:8080/users?id=1&username=icuxika&localDate=2024-05-01&localTime=16:58:00&localDateTime=2024-05-01 16:58:00")
        println(apiData)
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun postJsonTest() = runTest {
        // ApiData<User> postJsonTest(@RequestBody User user)
        val apiData = suspendPost<ApiData<User>>(
            "http://127.0.0.1:8080/users",
            User(1, "icuxika", LocalDate.of(2024, 5, 20), LocalTime.of(17, 12), LocalDateTime.of(2024, 5, 20, 17, 12))
        )
        println(apiData)
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun postFileTest() = runTest {
        // ApiData<User> postFileTest(@RequestPart("file") MultipartFile file, @RequestParam("id") Long id)
        val apiData = suspendPost<ApiData<Unit>>(
            "http://127.0.0.1:8080/users/upload", mapOf(
                Api.REQUEST_KEY_FILE to File("build.gradle.kts"),
                "id" to "11"
            )
        )
        println(apiData)
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun postFileListTest() = runTest {
        // ApiData<User> postFileListTest(@RequestPart("fileList") List<MultipartFile> fileList, @RequestParam("id") Long id, @RequestParam("time") LocalDateTime time)
        val apiData = suspendPost<ApiData<Unit>>(
            "http://127.0.0.1:8080/users/upload", mapOf(
                Api.REQUEST_KEY_FILE_LIST to listOf(
                    File("build.gradle.kts"),
                    File("src/main/resources/application.properties")
                ),
                "id" to "11",
                "time" to "2024-05-01 16:58:00"
            )
        )
        println(apiData)
        assertEquals(10000, apiData.code, "请求失败")
    }

    @Test
    fun uploadFileTest() = runTest {
        suspendPostFileFlow<ApiData<Unit>>(
            "http://127.0.0.1:8080/users/upload", mapOf(
                Api.REQUEST_KEY_FILE to File("build.gradle.kts"),
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
                    println(it.result)
                }
            }
        }
    }

    @Test
    fun downloadFileTest() = runTest {
        suspendGetFileFlow(
            "http://127.0.0.1:8080/users/download",
            Path.of(System.getProperty("user.home")).resolve("Downloads").resolve("temp").resolve("build.gradle.kts")
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