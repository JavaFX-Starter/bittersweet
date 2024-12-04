package com.icuxika.bittersweet.demo.controller

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.dsl.*
import com.icuxika.bittersweet.demo.annotation.AppFXML
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import java.net.URL
import java.util.*

@AppFXML(fxml = "fxml/fxgl.fxml")
class FXGLController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    enum class Type {
        DROPLET,
        BUCKET
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        container.top = HBox(Label("FXGL演示")).apply {
            alignment = Pos.CENTER
        }
        val fxglPaneContainer = StackPane()
        container.center = fxglPaneContainer.apply {
            children.add(
                GameApplication.embeddedLaunch(object : GameApplication() {
                    override fun initSettings(settings: GameSettings?) {
                        settings?.let {
                            it.isProfilingEnabled = false
                        }
                    }

                    override fun initGame() {
                        val bucket = entityBuilder()
                            .type(Type.BUCKET)
                            .at(getAppWidth() / 2.0, getAppHeight() - 200.0)
                            .viewWithBBox(Rectangle(80.0, 200.0, Color.DODGERBLUE))
                            .collidable()
                            .buildAndAttach()
                        bucket.xProperty().bind(getInput().mouseXWorldProperty())

                        FXGL.run({
                            entityBuilder()
                                .type(Type.DROPLET)
                                .at(FXGLMath.random(0, getAppWidth() - 64).toDouble(), 0.0)
                                .viewWithBBox(Circle(12.0, 12.0, 12.0, Color.BLACK))
                                .collidable()
                                .buildAndAttach()
                        }, Duration.seconds(1.0))

                    }

                    override fun onUpdate(tpf: Double) {
                        getGameWorld().getEntitiesByType(Type.DROPLET)
                            .forEach { droplet -> droplet.translateY(100 * tpf) }
                    }

                    override fun initPhysics() {
                        onCollisionBegin(Type.BUCKET, Type.DROPLET) { bucket, droplet ->
                            droplet.removeFromWorld()
                        }
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