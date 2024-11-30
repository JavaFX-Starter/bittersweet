package com.icuxika.bittersweet.skin

import com.icuxika.bittersweet.control.KButton
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.skin.ButtonSkin
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class KButtonSkin(kButton: KButton) : ButtonSkin(kButton) {

    private val queue: Queue<Circle> = LinkedList()

    private val container = AnchorPane()

    init {
        children.add(0, container)

        container.backgroundProperty().bind(Bindings.createObjectBinding({
            Background(BackgroundFill(kButton.getButtonBackground(), CornerRadii.EMPTY, Insets.EMPTY))
        }, kButton.buttonBackgroundProperty()))

        val badge = Label().apply {
            styleClass.add("k-button-badge")
            contentDisplay = ContentDisplay.CENTER
            textProperty().bind(kButton.buttonBadgeTextProperty())
            textFillProperty().bind(kButton.buttonBadgeTextFillProperty())
            visibleProperty().bind(kButton.buttonBadgeVisibleProperty())
            graphic = Circle().apply {
                fillProperty().bind(kButton.buttonBadgeBackgroundProperty())
                strokeProperty().bind(kButton.buttonBadgeBackgroundProperty())
                // 变更圆的半径时，需要考虑园内显示的文字的大小，为避免被父元素的字体尺寸影响，需要通过css的!important来修改
                radiusProperty().bind(kButton.buttonBadgeRadiusProperty())
            }
        }
        AnchorPane.setTopAnchor(badge, -abs(kButton.getButtonBadgeRadius().toDouble()))
        AnchorPane.setRightAnchor(badge, -abs(kButton.getButtonBadgeRadius().toDouble()))
        container.children.add(badge)

        kButton.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
            val centerX = event.x
            val centerY = event.y
            queue.add(Circle(centerX, centerY, 0.0).apply {
                fillProperty().bind(Bindings.createObjectBinding({
                    var color = Color.TRANSPARENT
                    kButton.textFill.let { paint ->
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
        container.resizeRelocate(
            skinnable.layoutBounds.minX,
            skinnable.layoutBounds.minY,
            skinnable.width,
            skinnable.height
        )
    }
}