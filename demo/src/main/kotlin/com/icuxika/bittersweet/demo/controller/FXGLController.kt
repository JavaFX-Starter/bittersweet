package com.icuxika.bittersweet.demo.controller

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.components.view.TextViewComponent
import com.almasb.fxgl.entity.Entity
import com.icuxika.bittersweet.demo.annotation.AppFXML
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.net.URL
import java.util.*

@AppFXML(fxml = "fxml/fxgl.fxml")
class FXGLController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    lateinit var player: Entity

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        container.top = HBox(Label("FXGL演示")).apply {
            alignment = Pos.CENTER
        }
        val fxglPaneContainer = StackPane()
        container.center = fxglPaneContainer.apply {
            children.add(
                GameApplication.embeddedLaunch(object : GameApplication() {
                    override fun initSettings(settings: GameSettings?) {
                    }

                    override fun initGame() {
                        player = FXGL.entityBuilder()
                            .at(10.0, 10.0)
                            .view(Rectangle(36.0, 36.0, Color.DODGERBLUE))
                            .with(TextViewComponent(0.0, 64.0, "人物名字"))
                            .view("application.png")
                            .buildAndAttach()
                    }
                }).apply {
                    prefWidthProperty().bind(fxglPaneContainer.widthProperty())
                    prefHeightProperty().bind(fxglPaneContainer.heightProperty())
                    renderWidthProperty().bind(fxglPaneContainer.widthProperty())
                    renderHeightProperty().bind(fxglPaneContainer.heightProperty())
                }
            )
        }
    }
}