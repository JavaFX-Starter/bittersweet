package com.icuxika.bittersweet.demo.component

import javafx.animation.Transition
import javafx.beans.property.DoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.transform.Translate
import javafx.util.Duration

class LayoutAnimator : ChangeListener<Number>, ListChangeListener<Node> {

    private val nodeXTransitions = hashMapOf<Node, MoveXTransition>()
    private val nodeYTransitions = hashMapOf<Node, MoveYTransition>()

    override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number?) {
        if (observable != null && oldValue != null && newValue != null) {
            val delta = newValue.toDouble() - oldValue.toDouble()
            val doubleProperty = observable as DoubleProperty
            val node = doubleProperty.bean as Node

            when (doubleProperty.name) {
                "layoutX" -> {
                    var moveXTransition = nodeXTransitions[node]
                    if (moveXTransition == null) {
                        moveXTransition = MoveXTransition(node)
                        nodeXTransitions[node] = moveXTransition
                    }
                    moveXTransition.setFromX(moveXTransition.getTranslateX() - delta)
                    moveXTransition.playFromStart()
                }

                "layoutY" -> {
                    var moveYTransition = nodeYTransitions[node]
                    if (moveYTransition == null) {
                        moveYTransition = MoveYTransition(node)
                        nodeYTransitions[node] = moveYTransition
                    }
                    moveYTransition.setFromY(moveYTransition.getTranslateY() - delta)
                    moveYTransition.playFromStart()
                }
            }
        }
    }

    override fun onChanged(c: ListChangeListener.Change<out Node>?) {
        c?.let { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    change.addedSubList.forEach {
                        observe(it)
                    }
                } else if (change.wasRemoved()) {
                    change.removed.forEach {
                        unobserve(it)
                    }
                }
            }
        }
    }

    fun observe(nodes: ObservableList<Node>) {
        nodes.forEach {
            this.observe(it)
        }
        nodes.addListener(this)
    }

    fun unobserve(nodes: ObservableList<Node>) {
        nodes.removeListener(this)
    }

    fun observe(node: Node) {
        node.layoutXProperty().addListener(this)
        node.layoutYProperty().addListener(this)
    }

    fun unobserve(node: Node) {
        node.layoutXProperty().removeListener(this)
        node.layoutYProperty().removeListener(this)
    }

    abstract class MoveTransition(node: Node) : Transition() {
        protected var translate: Translate

        init {
            cycleDuration = MOVEMENT_ANIMATION_DURATION
            translate = Translate()
            node.transforms.add(translate)
        }

        companion object {
            private val MOVEMENT_ANIMATION_DURATION = Duration(1000.0)
        }

        fun getTranslateX() = translate.x

        fun getTranslateY() = translate.y
    }

    class MoveXTransition(node: Node) : MoveTransition(node) {
        private var fromX: Double = 0.0
        override fun interpolate(frac: Double) {
            translate.x = (fromX * (1 - frac))
        }

        fun setFromX(fromX: Double) {
            translate.x = fromX
            this.fromX = fromX
        }
    }

    class MoveYTransition(node: Node) : MoveTransition(node) {
        private var fromY: Double = 0.0
        override fun interpolate(frac: Double) {
            translate.y = (fromY * (1 - frac))
        }

        fun setFromY(fromY: Double) {
            translate.y = fromY
            this.fromY = fromY
        }
    }
}