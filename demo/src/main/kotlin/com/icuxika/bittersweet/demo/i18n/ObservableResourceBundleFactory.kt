package com.icuxika.bittersweet.demo.i18n

import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.util.*

class ObservableResourceBundleFactory {

    private val resourceBundle: ObjectProperty<ResourceBundle> = SimpleObjectProperty()

    fun resourceBundleProperty() = resourceBundle

    fun getResourceBundle(): ResourceBundle = resourceBundle.get()

    fun setResourceBundle(resourceBundle: ResourceBundle) {
        this.resourceBundle.set(resourceBundle)
    }

    fun getStringBinding(key: String): StringBinding = object : StringBinding() {

        init {
            bind(resourceBundle)
        }

        override fun computeValue() = getResourceBundle().getString(key)
    }
}