package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.demo.system.Theme
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class AppView<T : Any>(controllerClass: KClass<T>) {

    private var stage: Stage? = null
    private var scene: Scene? = null
    private lateinit var fxmlLoader: FXMLLoader
    private lateinit var id: String
    private lateinit var rootNode: Parent

    init {
        controllerClass.findAnnotation<AppFXML>()?.let { appFXML ->
            val fxmlPath = appFXML.fxml
            id = fxmlPath.substring(fxmlPath.lastIndexOf("/") + 1)
                .substring(0, fxmlPath.lastIndexOf(".") - fxmlPath.lastIndexOf("/") - 1)
            AppResource.loadURL(fxmlPath)?.let { fxmlURL ->
                fxmlLoader = FXMLLoader(fxmlURL)
                rootNode = fxmlLoader.load()
            }
        }
    }

    fun setStage(stage: Stage): AppView<T> {
        this.stage = stage
        return this
    }

    fun setScene(scene: Scene): AppView<T> {
        this.scene = scene
        return this
    }

    fun show() {
        if (stage == null) {
            stage = Stage()
        }
        if (scene == null) {
            scene = Scene(rootNode)
        }
        sceneViewMap[scene!!] = id
        stage!!.scene = scene
        stage!!.show()
    }

    companion object {
        val sceneViewMap = hashMapOf<Scene, String>()

        init {
            AppResource.appThemeProperty().subscribe { newTheme ->
                newTheme?.let {
                    sceneViewMap.forEach { (s, v) ->
                        val light = "css/$v.css"
                        val dark = "css/$v-dark.css"
                        s.stylesheets.removeAll(
                            AppResource.loadURL(light)?.toExternalForm(),
                            AppResource.loadURL(dark)?.toExternalForm()
                        )
                        if (it == Theme.LIGHT) {
                            s.stylesheets.add(AppResource.loadURL(light)?.toExternalForm())
                        } else {
                            s.stylesheets.add(AppResource.loadURL(dark)?.toExternalForm())
                        }
                    }
                }
            }
        }
    }
}