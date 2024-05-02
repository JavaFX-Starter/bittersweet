package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.demo.MainApp.Companion.LOGGER
import com.icuxika.bittersweet.demo.controller.MainController
import com.icuxika.bittersweet.demo.system.Theme
import com.icuxika.bittersweet.extension.logger
import io.github.palexdev.materialfx.theming.JavaFXThemes
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets
import io.github.palexdev.materialfx.theming.UserAgentBuilder
import javafx.application.Application
import javafx.stage.Stage
import java.util.*

fun main(args: Array<String>) {
    MainApp.Companion::class.java.getResourceAsStream("/application.properties").use {
        Properties().apply { load(it) }
    }.let { properties ->
        LOGGER.info("当前环境：${properties["environment"]}")
    }
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {

    override fun start(primaryStage: Stage?) {
        UserAgentBuilder.builder()
            .themes(JavaFXThemes.MODENA)
            .themes(MaterialFXStylesheets.forAssemble(false))
            .setDeploy(true)
            .setResolveAssets(true)
            .build()
            .setGlobal()

        val mainView = AppView(MainController::class)

        primaryStage?.apply {
            AppResource.setTheme(Theme.LIGHT)
            AppResource.setLanguage(Locale.SIMPLIFIED_CHINESE)
            titleProperty().bind(AppResource.getLanguageBinding("title"))
            mainView.setStage(this).show()
        }
    }

    companion object {
        val LOGGER = logger()
    }
}