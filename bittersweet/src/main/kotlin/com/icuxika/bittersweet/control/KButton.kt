package com.icuxika.bittersweet.control

import com.icuxika.bittersweet.skin.KButtonSkin
import javafx.scene.control.Button
import javafx.scene.control.Skin

class KButton : Button() {

    init {
        styleClass.add(STYLE_CLASS)
    }

    override fun createDefaultSkin(): Skin<*> = KButtonSkin(this)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getUserAgentStylesheet(): String = javaClass.getResource(STYLESHEET).toExternalForm()

    companion object {
        const val STYLE_CLASS = "k-button"
        const val STYLESHEET = "k-button.css"
    }
}