package com.icuxika.bittersweet.commons.serialization

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class TypeBase<T>

inline fun <reified T> typeTokenOf(): Type {
    val base = object : TypeBase<T>() {}
    val superType = base::class.java.genericSuperclass!!
    return (superType as ParameterizedType).actualTypeArguments.first()
}