package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.demo.system.Theme
import javafx.application.ColorScheme
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import java.net.URL

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

    fun setTheme(value: Theme) {
        theme.set(value)
    }

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