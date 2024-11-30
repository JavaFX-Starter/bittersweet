package com.icuxika.bittersweet.control

import com.icuxika.bittersweet.skin.KButtonSkin
import javafx.css.*
import javafx.scene.control.Button
import javafx.scene.control.Skin
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

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

    // 背景色 ----------------------------------------
    private val backgroundMetadata: CssMetaData<KButton, Paint> = object :
        CssMetaData<KButton, Paint>("-k-button-background", StyleConverter.getPaintConverter(), Color.TRANSPARENT) {
        override fun isSettable(styleable: KButton): Boolean {
            return !styleable.buttonBackground.isBound
        }

        override fun getStyleableProperty(styleable: KButton): StyleableProperty<Paint> {
            return styleable.buttonBackground
        }
    }
    private val buttonBackground =
        SimpleStyleableObjectProperty(backgroundMetadata, this@KButton, "buttonBackground", Color.TRANSPARENT)

    fun buttonBackgroundProperty() = buttonBackground
    fun getButtonBackground() = buttonBackground.get()
    fun setButtonBackground(color: Paint) {
        buttonBackground.set(color)
    }
    // 背景色 ----------------------------------------

    override fun getControlCssMetaData(): MutableList<CssMetaData<out Styleable, *>> {
        return mutableListOf(backgroundMetadata)
    }

    override fun createDefaultSkin(): Skin<*> = KButtonSkin(this)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getUserAgentStylesheet(): String = javaClass.getResource(STYLESHEET).toExternalForm()

    companion object {
        const val STYLE_CLASS = "k-button"
        const val STYLESHEET = "k-button.css"
    }
}