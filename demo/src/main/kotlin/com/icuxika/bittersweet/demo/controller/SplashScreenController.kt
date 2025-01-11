package com.icuxika.bittersweet.demo.controller

import com.icuxika.bittersweet.demo.annotation.AppFXML
import javafx.animation.PathTransition
import javafx.animation.PauseTransition
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.util.Duration
import java.net.URL
import java.util.*

@AppFXML(fxml = "fxml/splash-screen.fxml")
class SplashScreenController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    private val circle = Circle(8.0, Color.DODGERBLUE)
    private val path = Path().apply {
        stroke = Color.BLACK
        strokeLineCap = StrokeLineCap.ROUND
        strokeDashArray.addAll(4.0, 4.0)
        strokeWidth = 2.0
        fill = null
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val pane = Pane()
        container.center = pane.apply {
            children.addAll(circle, path)
        }

        val pathTransition = PathTransition().apply {
            node = circle
            duration = Duration.seconds(3.0)
            cycleCount = PathTransition.INDEFINITE
            isAutoReverse = false
            orientation = PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT
        }

        val pauseTransition = PauseTransition()
        pauseTransition.setOnFinished {
            pathTransition.path = path
            pathTransition.play()
        }

        pane.layoutBoundsProperty().addListener { _, _, newValue ->
            if (newValue.width > 0 && newValue.height > 0) {
                pathTransition.stop()
                path.elements.clear()
                path.elements.addAll(
                    MoveTo(newValue.width / 2, newValue.height / 2),
                    CubicCurveTo(
                        0.0, 0.0,
                        0.0, newValue.height,
                        newValue.width / 2, newValue.height / 2
                    ),
                    CubicCurveTo(
                        newValue.width, 0.0,
                        newValue.width, newValue.height,
                        newValue.width / 2, newValue.height / 2
                    ),
                )
                pauseTransition.playFromStart()
            }
        }
    }

}