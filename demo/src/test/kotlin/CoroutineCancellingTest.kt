import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.test.Test

class CoroutineCancellingTest {

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun cancellationTest() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler 捕获到异常 ${exception.message}")
        }
        CoroutineScope(newSingleThreadContext("cancellationTest")).launch(handler) {
            launch {
                try {
                    println("子协程1")
                    delay(10000)
                } catch (e: Exception) {
                    println("子协程1捕获到异常[${e.message}]")
                } finally {
                    withContext(NonCancellable) {
                        println("子协程1开始清理")
                        delay(3000)
                        println("子协程1结束清理")
                    }
                }
            }
            launch {
                println("子协程2")
                delay(7000)
                throw RuntimeException("抛出RuntimeException")
            }
            launch {
                try {
                    println("子协程3")
                    delay(3000)
                } catch (e: Exception) {
                    println("子协程3捕获到异常[${e.message}]")
                } finally {
                    withContext(NonCancellable) {
                        println("子协程3开始清理")
                        delay(2000)
                        println("子协程3结束清理")
                    }
                }
            }
            launch {
                println("子协程4")
                delay(1000)
                throw CancellationException("抛出CancellationException")
            }
        }.join()
    }

    @Test
    fun cancelAndJoinTest() = runBlocking {
        val job1 = launch {
            println("子协程1 开始")
            try {
                while (isActive) {
                    println("子协程1 运行中")
                    delay(500)
                }
                println("子协程1 结束 -- 不被执行")
            } catch (e: Exception) {
                println("捕获到异常 ${e.message}")
            } finally {
                println("子协程1 结束")
            }
        }
        val job2 = launch {
            println("子协程2 开始")
            thread {
                repeat(10) {
                    if (!isActive) {
                        println("子协程2 取消")
                        throw CancellationException("抛出CancellationException")
                    }
                    println("子协程2 运行中")
                    Thread.sleep(500)
                }
            }
            println("子协程2 结束")
        }
        launch {
            println("子协程0 开始")
            val startTime = System.currentTimeMillis()
            while (isActive && (System.currentTimeMillis() - startTime < 5000)) {
                println("子协程0 运行中")
                delay(500)
            }
            println("子协程0 结束")
        }
        delay(1000)
        job1.cancelAndJoin()
        job2.cancelAndJoin()
    }

}