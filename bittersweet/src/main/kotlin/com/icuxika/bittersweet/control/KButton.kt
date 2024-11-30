package com.icuxika.bittersweet.control

import com.icuxika.bittersweet.skin.KButtonSkin
import javafx.scene.control.Button
import javafx.scene.control.Skin

class KButton() : Button() {

    init {
        styleClass.add(STYLE_CLASS)
    }

    constructor(text: String) : this() {
        super.setText(text)
    }

    constructor(text: String, prefWidth: Double, prefHeight: Double) : this() {
        super.setText(text)
        super.setPrefSize(prefWidth, prefHeight)
    }

    override fun createDefaultSkin(): Skin<*> = KButtonSkin(this)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getUserAgentStylesheet(): String = javaClass.getResource(STYLESHEET).toExternalForm()

    companion object {
        const val STYLE_CLASS = "k-button"
        const val STYLESHEET = "k-button.css"
    }
}