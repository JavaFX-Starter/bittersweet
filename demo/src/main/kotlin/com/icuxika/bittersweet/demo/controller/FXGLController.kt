package com.icuxika.bittersweet.demo.controller

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.Spawns
import com.icuxika.bittersweet.demo.annotation.AppFXML
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
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

    private val score = SimpleIntegerProperty(0)
    lateinit var player: Entity

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        container.background = Background(BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY))

        container.top = VBox(
            Label("FXGL演示").apply {
                textFill = Color.WHITE
            },
            Label().apply {
                textFill = Color.WHITE
                textProperty().bind(SimpleStringProperty("得分 ").concat(score.asString()))
            }
        ).apply {
            alignment = Pos.CENTER
            background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY))
        }

        container.bottom = HBox(
            Label("FXGL演示").apply {
                textFill = Color.WHITE
            }
        ).apply {
            alignment = Pos.CENTER
            background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY))
        }

        val fxglPaneContainer = StackPane().apply {
            minWidth = 640.0
            minHeight = 640.0
            maxWidth = 640.0
            maxHeight = 640.0
        }
        container.center = fxglPaneContainer.apply {
            children.add(
                GameApplication.embeddedLaunch(object : GameApplication() {
                    override fun initSettings(settings: GameSettings?) {
                        settings?.let {
                            it.isProfilingEnabled = false
                            it.isScaleAffectedOnResize = false
                            it.width = 640
                            it.height = 640
                        }
                    }

                    override fun initGame() {
                        FXGL.setLevelFromMap("map.tmx")

                        getGameWorld().addEntityFactory(object : EntityFactory {
                            @Spawns("background")
                            fun newBackground(data: SpawnData): Entity {
                                return entityBuilder(data)
                                    .view(
                                        ScrollingBackgroundView(
                                            FXGL.image(
                                                "background.png",
                                                getAppWidth().toDouble(),
                                                getAppHeight().toDouble()
                                            ),
                                            getAppWidth().toDouble(),
                                            getAppHeight().toDouble(),
                                            Orientation.HORIZONTAL
                                        )
                                    )
                                    .build()
                            }
                        })

                        spawn("background")

                        FXGL.run({
                            entityBuilder()
                                .type(Type.DROPLET)
                                .at(FXGLMath.random(0, getAppWidth() - 64).toDouble(), 0.0)
                                .viewWithBBox(Circle(12.0, 12.0, 12.0, Color.BLACK))
                                .collidable()
                                .buildAndAttach()
                        }, Duration.seconds(1.0))

                        player = entityBuilder()
                            .type(Type.BUCKET)
                            .at(0.0, getAppHeight() * 2 / 3.0)
                            .viewWithBBox(texture("boat.png", 80.0, 24.0))
                            .collidable()
                            .buildAndAttach()

                        getGameScene().viewport.apply {
                            isLazy = true
                            bindToEntity(
                                player,
                                getAppWidth() / 2.0,
                                getAppHeight() * 2 / 3.0
                            )
                        }
                    }

                    override fun onUpdate(tpf: Double) {
                        getGameWorld().getEntitiesByType(Type.DROPLET)
                            .forEach { droplet -> droplet.translateY(100 * tpf) }
                    }

                    override fun initInput() {
                        onKey(KeyCode.LEFT) {
                            player.translateX(-10.0)
                        }
                        onKey(KeyCode.RIGHT) {
                            player.translateX(10.0)
                        }
                    }

                    override fun initPhysics() {
                        onCollisionBegin(Type.BUCKET, Type.DROPLET) { bucket, droplet ->
                            droplet.removeFromWorld()
                            score.set(score.get() + 1)
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