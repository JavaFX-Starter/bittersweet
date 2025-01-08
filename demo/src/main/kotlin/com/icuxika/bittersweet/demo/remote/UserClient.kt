package com.icuxika.bittersweet.demo.remote

import com.icuxika.bittersweet.demo.annotation.ApiClient
import com.icuxika.bittersweet.demo.annotation.ApiRequest
import com.icuxika.bittersweet.demo.api.Api
import com.icuxika.bittersweet.demo.api.ApiData
import com.icuxika.bittersweet.demo.model.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ApiClient
interface UserClient {

    @ApiRequest(value = "/users")
    fun getUser(
        id: Long,
        username: String,
        localDate: LocalDate,
        localTime: LocalTime,
        localDateTime: LocalDateTime
    ): ApiData<User>

    @ApiRequest(method = Api.HTTPRequestMethod.POST, value = "/users")
    fun postUser(user: User): ApiData<User>
}