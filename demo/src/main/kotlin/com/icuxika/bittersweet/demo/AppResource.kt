package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.demo.i18n.ObservableResourceBundleFactory
import com.icuxika.bittersweet.demo.system.Theme
import javafx.application.ColorScheme
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import java.net.URL
import java.util.*

object AppResource {

    private val appTheme = SimpleObjectProperty<Theme>()

    fun appThemeProperty(): SimpleObjectProperty<Theme> {
        return appTheme
    }

    private fun setAppTheme(value: Theme) {
        appTheme.set(value)
    }

    /**
     * 主题，用于交互，系统实际根据[appTheme]的变化来更新主题
     */
    private val theme = SimpleObjectProperty<Theme>()

    fun themeProperty(): SimpleObjectProperty<Theme> {
        return theme
    }

    fun getTheme(): Theme = theme.get()

    fun setTheme(value: Theme) {
        theme.set(value)
    }

    private const val LANGUAGE_RESOURCE_NAME = "LanguageResource"

    val SUPPORT_LANGUAGE_LIST = listOf(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)

    private val LANGUAGE_RESOURCE_FACTORY = ObservableResourceBundleFactory()

    fun setLanguage(locale: Locale) {
        LANGUAGE_RESOURCE_FACTORY.setResourceBundle(ResourceBundle.getBundle(LANGUAGE_RESOURCE_NAME, locale))
    }

    fun getLanguageBinding(key: String) = LANGUAGE_RESOURCE_FACTORY.getStringBinding(key)

    init {
        // 监听主题变化
        theme.subscribe { newTheme ->
            newTheme?.let {
                if (it == Theme.SYSTEM) {
                    setAppTheme(
                        if (Platform.getPreferences().colorScheme == ColorScheme.LIGHT) Theme.LIGHT
                        else Theme.DARK
                    )
                } else {
                    setAppTheme(newTheme)
                }
            }
        }
        // 监听系统主题变化
        Platform.getPreferences().colorSchemeProperty().subscribe { colorScheme ->
            colorScheme?.let {
                when (it) {
                    ColorScheme.LIGHT -> {
                        if (theme.value == Theme.SYSTEM) {
                            setAppTheme(Theme.LIGHT)
                        }
                    }

                    ColorScheme.DARK -> {
                        if (theme.value == Theme.SYSTEM) {
                            setAppTheme(Theme.DARK)
                        }
                    }
                }
            }
        }
    }

    fun loadURL(path: String): URL? {
        return AppResource.javaClass.getResource(path)
    }
}