package com.icuxika.bittersweet.skin

import com.icuxika.bittersweet.control.KButton
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.scene.control.skin.ButtonSkin
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.util.Duration
import java.util.*
import kotlin.math.min

class KButtonSkin(kButton: KButton) : ButtonSkin(kButton) {

    private val queue: Queue<Circle> = LinkedList()

    private val stackPane = StackPane().apply {
        style = """
            -fx-background-color: #EAEAEA;
        """.trimIndent()
    }

    init {
        children.add(stackPane)

        kButton.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
            val centerX = event.x
            val centerY = event.y
            queue.add(Circle(centerX, centerY, 0.0).apply {
                fill = Color.TRANSPARENT
                stroke = Color.DODGERBLUE
            })
        }
        kButton.addEventHandler(MouseEvent.MOUSE_RELEASED) { event ->
            queue.poll().let { circle ->
                children.add(circle)
                Timeline(
                    KeyFrame(
                        Duration.millis(250.0),
                        {
                            children.remove(circle)
                        },
                        KeyValue(
                            circle.radiusProperty(),
                            min(skinnable.layoutBounds.width, skinnable.layoutBounds.height)
                        )
                    )
                ).play()
            }
        }
    }

    override fun layoutChildren(x: Double, y: Double, w: Double, h: Double) {
        super.layoutChildren(x, y, w, h)
        stackPane.resizeRelocate(
            skinnable.layoutBounds.minX,
            skinnable.layoutBounds.minY,
            skinnable.width,
            skinnable.height
        )
    }
}