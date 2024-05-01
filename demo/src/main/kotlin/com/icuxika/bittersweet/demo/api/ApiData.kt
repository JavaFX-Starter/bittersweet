package com.icuxika.bittersweet.demo.api

data class ApiData<T>(
    var code: Int = 10000,
    var msg: String = "后端未返回",
    var data: T? = null
)