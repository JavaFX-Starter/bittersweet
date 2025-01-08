package com.icuxika.bittersweet.demo.annotation

import com.icuxika.bittersweet.demo.api.Api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiClient()

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiRequest(val method: Api.HTTPRequestMethod = Api.HTTPRequestMethod.GET, val value: String)
