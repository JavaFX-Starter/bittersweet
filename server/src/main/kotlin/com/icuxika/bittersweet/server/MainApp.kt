package com.icuxika.bittersweet.server

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun main(args: Array<String>) = runBlocking {
    CoroutineScope(newSingleThreadContext("miniByteTurbo")).launch {
        val miniByteTurbo = MiniByteTurbo()
        miniByteTurbo.start()
        println("[${currentCoroutineInfo()}] hello")
        doSendData(miniByteTurbo)
        println("[${currentCoroutineInfo()}] world")
    }
    io.ktor.server.netty.EngineMain.main(args)
}

suspend fun doSendData(miniByteTurbo: MiniByteTurbo) = coroutineScope {
    launch {
        delay(3000)
        println("[${currentCoroutineInfo()}] sendData: 1")
        miniByteTurbo.sendData(1)
    }
    launch {
        delay(2000)
        println("[${currentCoroutineInfo()}] sendData: 2")
        miniByteTurbo.sendData(2)
    }
    launch {
        delay(1000)
        println("[${currentCoroutineInfo()}] sendData: 3")
        miniByteTurbo.sendData(3)
    }
}

@OptIn(ExperimentalStdlibApi::class)
suspend fun currentCoroutineInfo() =
    "${currentCoroutineContext()[CoroutineDispatcher]}###${Thread.currentThread().name}"

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class MiniByteTurbo {
    private val readDispatcher = newSingleThreadContext("readDispatcher")
    private val writeDispatcher = newSingleThreadContext("writeDispatcher")
    private val readScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = readDispatcher + CoroutineName("readScope")
    }
    private val writeScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = writeDispatcher + CoroutineName("writeScope")
    }
    private val readJob = readScope.launch(start = CoroutineStart.LAZY) {
        doRead()
    }
    private val writeJob = writeScope.launch(start = CoroutineStart.LAZY) {
        doWrite()
    }
    private val channel = Channel<Int>()

    private fun doRead() {

    }

    private suspend fun doWrite() {
        while (writeJob.isActive) {
            val data = channel.receive()
            println("[${currentCoroutineInfo()}] data: $data")
        }
    }

    fun start() {
        writeJob.start()
    }

    suspend fun sendData(data: Int) {
        channel.send(data)
    }
}