package com.icuxika.bittersweet.delegate

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

class PropertyDelegate<T>(val fxProperty: Property<T>) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return fxProperty.value
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        fxProperty.value = value
    }

}

fun <T> property(value: T? = null) = PropertyDelegate(SimpleObjectProperty<T>(value))

fun <T> Any.getProperty(prop: KMutableProperty1<*, T>): ObjectProperty<T> {
    val field = requireNotNull(javaClass.findFieldByName("${prop.name}\$delegate")) {
        "Property ${prop.name} not found"
    }
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val delegate = field.get(this) as PropertyDelegate<T>
    return delegate.fxProperty as ObjectProperty<T>
}

fun Class<*>.findFieldByName(name: String): Field? {
    (declaredFields + fields).firstOrNull { it.name == name }?.let { return it }
    if (superclass == java.lang.Object::class.java) return null
    return superclass.findFieldByName(name)
}