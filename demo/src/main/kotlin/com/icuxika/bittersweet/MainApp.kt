package com.icuxika.bittersweet

import com.icuxika.bittersweet.control.KButton
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage?.apply {
            scene = Scene(KButton().apply {
                setPrefSize(248.0, 84.0)
            })
        }?.show()
    }
}