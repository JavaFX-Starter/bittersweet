package com.icuxika.bittersweet.demo.api

import com.google.gson.reflect.TypeToken
import com.icuxika.bittersweet.demo.api.Api.HTTPRequestMethod
import com.icuxika.bittersweet.demo.util.Global.gson
import javafx.scene.control.ProgressIndicator
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.*
import java.io.File
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.Duration

inline fun <reified T : Any> request(
    url: String,
    method: HTTPRequestMethod = HTTPRequestMethod.GET,
    data: Any? = null,
): ApiExecutor<T> {
    val type = object : TypeToken<T>() {}.type
    return Api.request(type, url, method, data)
}

object Api {
    enum class HTTPRequestMethod {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE
    }

    fun <T> request(
        type: Type,
        url: String,
        method: HTTPRequestMethod = HTTPRequestMethod.GET,
        data: Any? = null,
    ): ApiExecutor<T> {
        var requestData: RequestData? = null
        if (method == HTTPRequestMethod.POST) {
            data?.let {
                when (it) {
                    is String -> {
                        requestData = JsonRequestData(it)
                    }

                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        requestData = MultipartBodyRequestData(it as Map<String, Any>)
                    }

                    else -> {
                        requestData = JsonRequestData(gson.toJson(it))
                    }
                }
            }
        }
        return ApiExecutor(ApiHelper.buildRequest(url, method, requestData), type)
    }

    const val REQUEST_KEY_LISTENER = "__listener"
    const val REQUEST_KEY_FILE = "__file"
    const val REQUEST_KEY_FILE_LIST = "__file__list"
}

interface RequestData {
    val data: Any
}

class JsonRequestData(private val json: String) : RequestData {
    override val data: String
        get() = json
}

class MultipartBodyRequestData(private val map: Map<String, Any>) : RequestData {
    override val data: Map<String, Any>
        get() = map
}

object ApiHelper {
    /**
     * 构建请求
     */
    fun buildRequest(url: String, method: HTTPRequestMethod, requestData: RequestData? = null): Request {
        val requestBuilder = Request.Builder()
        requestBuilder.url(url)
        when (method) {
            HTTPRequestMethod.GET -> {}
            HTTPRequestMethod.HEAD -> {}
            HTTPRequestMethod.POST -> {
                requestData?.let {
                    when (it) {
                        is JsonRequestData -> {
                            requestBuilder.post(it.data.toRequestBody("application/json".toMediaType()))
                        }

                        is MultipartBodyRequestData -> {
                            val multiBodyBuilder = MultipartBody.Builder()
                            multiBodyBuilder.setType(MultipartBody.FORM)
                            var requestListener: RequestListener? = null
                            it.data.forEach { (k, v) ->
                                if (k == Api.REQUEST_KEY_LISTENER) {
                                    @Suppress("UNCHECKED_CAST")
                                    requestListener = v as RequestListener
                                } else {
                                    if (k == Api.REQUEST_KEY_FILE && v is File) {
                                        multiBodyBuilder.addFormDataPart(
                                            "file",
                                            v.name,
                                            v.asRequestBody()
                                        )
                                    } else if (k == Api.REQUEST_KEY_FILE_LIST && v is List<*>) {
                                        v.forEach { file ->
                                            require(file is File)
                                            multiBodyBuilder.addFormDataPart(
                                                "fileList",
                                                file.name,
                                                file.asRequestBody()
                                            )
                                        }
                                    } else {
                                        multiBodyBuilder.addFormDataPart(k, v as String)
                                    }
                                }
                            }
                            requestBuilder.post(ObservableRequestBody(multiBodyBuilder.build(), requestListener))
                        }

                        else -> {}
                    }
                }
            }

            HTTPRequestMethod.PUT -> {}
            HTTPRequestMethod.DELETE -> {}
        }
        return requestBuilder.build()
    }
}

/**
 * 请求执行器
 */
class ApiExecutor<T>(private val request: Request, private val type: Type) {
    private var onSuccess: ((T) -> Unit)? = null
    private var onFailure: ((Exception) -> Unit)? = null

    fun success(block: (data: T) -> Unit): ApiExecutor<T> {
        onSuccess = block
        return this
    }

    fun failure(block: (exception: Exception) -> Unit): ApiExecutor<T> {
        onFailure = block
        return this
    }

    private fun doSuccess(data: T) {
        onSuccess?.invoke(data)
    }

    private fun doFailure(exception: Exception) {
        onFailure?.invoke(exception)
    }

    /**
     * 执行请求并根据类型解析响应
     */
    fun execute() {
        runCatching {
            val dataType = type as? ParameterizedType
                ?: throw RuntimeException("Type [${type.typeName}] is not a ParameterizedType")
            val actualTypeArguments = dataType.actualTypeArguments
            if (actualTypeArguments.isEmpty()) {
                throw RuntimeException("Type [${dataType.typeName}]'s actualTypeArguments is Empty")
            }
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    resolveResponse(response, type)
                } else {
                    doFailure(IOException("Unexpected code $response"))
                }
            }
        }.onFailure {
            doFailure(RuntimeException(it))
        }
    }

    /**
     * 直接返回响应流，用于Get请求从后端下载流文件
     */
    fun stream() {
        runCatching {
            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body
            if (responseBody == null) {
                doFailure(RuntimeException("请求响应体中没有文件数据"))
            } else {
                val contentLength: Double =
                    response.header("CONTENT-LENGTH")?.toDouble() ?: ProgressIndicator.INDETERMINATE_PROGRESS
                @Suppress("UNCHECKED_CAST")
                doSuccess(Pair(responseBody.byteStream(), contentLength) as T)
            }
        }.onFailure {
            doFailure(RuntimeException(it))
        }
    }

    /**
     * 解析响应
     */
    private fun resolveResponse(response: Response, type: ParameterizedType) {
        response.body?.string()?.let { responseBodyString ->
            var apiData: T
            runCatching {
                apiData = gson.fromJson(responseBodyString, type)
                if (apiData is ApiData<*>) {
                    // 这里不对业务错误进行判断
                    doSuccess(apiData)
                } else {
                    doFailure(RuntimeException("返回值的类型应当是[ApiData]"))
                }
            }.onFailure {
                doFailure(RuntimeException(it))
            }
        }
    }

    companion object {
        private val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(60))
            .build()
    }
}

typealias RequestListener = (workDone: Long, max: Long) -> Unit

/**
 * 监听上传进度
 */
class ObservableRequestBody(
    private val delegate: RequestBody,
    private val listener: RequestListener? = null
) : RequestBody() {

    private lateinit var observableSink: ObservableSink

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun writeTo(sink: BufferedSink) {
        observableSink = ObservableSink(sink)
        val bufferedSink = observableSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    override fun contentLength(): Long {
        return try {
            delegate.contentLength()
        } catch (e: Exception) {
            -1
        }
    }

    inner class ObservableSink(delegate: Sink) : ForwardingSink(delegate) {
        private var workDone: Long = 0
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            workDone += byteCount
            listener?.invoke(workDone, contentLength())
        }
    }
}

