package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.demo.annotation.ApiClient
import com.icuxika.bittersweet.demo.annotation.ApiRequest
import com.icuxika.bittersweet.demo.api.Api
import com.icuxika.bittersweet.demo.system.SystemProperties
import java.io.File
import java.lang.reflect.Proxy
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * API注册器类，负责扫描指定包下的所有类，并为带有[ApiClient]注解的类生成代理对象
 */
class ApiRegistrar {

    /**
     * 注册指定包名下的所有API类
     * @param packageName 需要注册的包名
     */
    fun register(packageName: String) {
        // 将包名转换为包路径
        val packagePath = packageName.replace(".", "/")
        // 获取当前线程的类加载器
        val contextClassLoader = Thread.currentThread().contextClassLoader
        // 获取指定包路径下的所有资源
        val resources = contextClassLoader.getResources(packagePath)
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            val url = resource.toExternalForm()
            if (url.startsWith("file:")) {
                // 处理在文件系统中的资源
                val file = File(url.substring("file:".length))
                if (file.isDirectory) {
                    // 遍历文件夹中的所有文件和子文件夹
                    file.walkTopDown().forEach {
                        if (it.isFile && it.name.endsWith(".class")) {
                            // 将.class文件的路径转换为类名
                            val className =
                                it.absolutePath.substring(file.absolutePath.length + 1, it.absolutePath.length - 6)
                            val fullClassName = packageName + '.' + className.replace(File.separatorChar, '.')
                            // 为该类创建代理对象
                            createProxy(contextClassLoader, fullClassName)
                        }
                    }
                }
            } else if (url.startsWith("jar:")) {
                // 处理在JAR文件中的资源
                val jarUrl = URI.create(url.substring("jar:".length, url.indexOf('!'))).toURL()
                val jarFile = JarFile(jarUrl.file)
                val entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    val entry: JarEntry = entries.nextElement()
                    val name = entry.name
                    if (name.startsWith(packagePath) && name.endsWith(".class")) {
                        // 将JAR文件中的类路径转换为类名
                        val className = name.substring(packagePath.length + 1, name.length - 6)
                        val fullClassName = packageName + '.' + className.replace(File.separatorChar, '.')
                        // 为该类创建代理对象
                        createProxy(contextClassLoader, fullClassName)
                    }
                }
            }
        }
    }

    /**
     * 为指定类名的类创建代理对象
     * @param contextClassLoader 类加载器
     * @param className 类名
     */
    private fun createProxy(contextClassLoader: ClassLoader, className: String) {
        val kClazz = Class.forName(className).kotlin
        kClazz.findAnnotation<ApiClient>()?.let { annotation ->
            // 使用动态代理为带有ApiClient注解的类生成代理对象
            proxyMap[kClazz] = Proxy.newProxyInstance(
                contextClassLoader,
                arrayOf(kClazz.java)
            ) { _, method, args ->
                // 处理代理对象的方法调用
                val apiRequest = method.getAnnotation(ApiRequest::class.java)
                var url = "${SystemProperties.serverUrl}${apiRequest.value}"
                when (apiRequest.method) {
                    Api.HTTPRequestMethod.GET -> {
                        // 构建GET请求的URL参数
                        val params = method.parameters.zip(args) { parameter, arg ->
                            val paramValue = when (arg) {
                                is LocalDate -> arg.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                is LocalTime -> arg.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                                is LocalDateTime -> arg.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                else -> arg.toString()
                            }
                            val encodedParamName = URLEncoder.encode(parameter.name, StandardCharsets.UTF_8.name())
                            val encodedParamValue = URLEncoder.encode(paramValue, StandardCharsets.UTF_8.name())
                            "$encodedParamName=$encodedParamValue"
                        }.joinToString("&")
                        url += "?$params"
                    }

                    else -> {}
                }
                var requestData: Any? = null
                when (apiRequest.method) {
                    Api.HTTPRequestMethod.POST -> {
                        requestData = args[0]
                    }

                    else -> {}
                }

                var result: Any? = null
                // 发起API请求并处理结果
                Api.request<Any>(method!!.genericReturnType, url, apiRequest.method, requestData)
                    .success { data ->
                        result = data
                    }.failure {
                        it.printStackTrace()
                    }.execute()
                result
            }
        }
    }

    /**
     * 存储生成的代理对象的伴生对象
     */
    companion object {
        // 代理对象映射，用于存储类与对应的代理对象
        val proxyMap = hashMapOf<KClass<*>, Any>()

        /**
         * 获取指定类的代理对象
         * @param kClass 类的KClass实例
         * @return 代理对象
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> getProxy(kClass: KClass<*>): T {
            return proxyMap[kClass]!! as T
        }
    }
}
