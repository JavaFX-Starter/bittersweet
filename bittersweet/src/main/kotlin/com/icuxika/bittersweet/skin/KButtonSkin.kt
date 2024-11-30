package com.icuxika.bittersweet.skin

import com.icuxika.bittersweet.control.KButton
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.binding.Bindings
import javafx.scene.control.skin.ButtonSkin
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import java.util.*
import kotlin.math.max

class KButtonSkin(kButton: KButton) : ButtonSkin(kButton) {

    private val queue: Queue<Circle> = LinkedList()

    private val stackPane = StackPane().apply {
        style = """
            -fx-background-color: dodgerblue;
        """.trimIndent()
    }

    init {
        children.add(0, stackPane)

        kButton.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
            val centerX = event.x
            val centerY = event.y
            queue.add(Circle(centerX, centerY, 0.0).apply {
                fillProperty().bind(Bindings.createObjectBinding({
                    var color = Color.TRANSPARENT
                    kButton.textFillProperty().get()?.let { paint ->
                        (paint as? Color)?.let {
                            color = Color(it.red, it.green, it.blue, 0.25)
                        }
                    }
                    color
                }, kButton.textFillProperty()))
                stroke = Color.TRANSPARENT
                strokeWidth = 1.5
                clip = Rectangle(0.0, 0.0, skinnable.width, skinnable.height)
            })
        }
        kButton.addEventHandler(MouseEvent.MOUSE_RELEASED) { event ->
            queue.poll().let { circle ->
                children.add(circle)
                Timeline(
                    KeyFrame(
                        Duration.millis(450.0),
                        {
                            children.remove(circle)
                        },
                        KeyValue(
                            circle.radiusProperty(),
                            max(skinnable.layoutBounds.width, skinnable.layoutBounds.height)
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