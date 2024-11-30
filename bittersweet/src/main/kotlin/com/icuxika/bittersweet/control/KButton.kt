package com.icuxika.bittersweet.control

import com.icuxika.bittersweet.skin.KButtonSkin
import javafx.beans.property.SimpleStringProperty
import javafx.css.*
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Skin
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

class KButton() : Button() {

    init {
        styleClass.add(STYLE_CLASS)
        alignment = Pos.CENTER
    }

    constructor(text: String) : this() {
        super.setText(text)
    }

    constructor(text: String, prefWidth: Double, prefHeight: Double) : this() {
        super.setText(text)
        super.setPrefSize(prefWidth, prefHeight)
    }

    // 背景色 ----------------------------------------
    private val buttonBackgroundMetadata: CssMetaData<KButton, Paint> = object :
        CssMetaData<KButton, Paint>("-k-button-background", StyleConverter.getPaintConverter(), Color.TRANSPARENT) {
        override fun isSettable(styleable: KButton): Boolean {
            return !styleable.buttonBackground.isBound
        }

        override fun getStyleableProperty(styleable: KButton): StyleableProperty<Paint> {
            return styleable.buttonBackground
        }
    }
    private val buttonBackground =
        SimpleStyleableObjectProperty(buttonBackgroundMetadata, this@KButton, "buttonBackground", Color.TRANSPARENT)

    fun buttonBackgroundProperty() = buttonBackground
    fun getButtonBackground() = buttonBackground.get()
    fun setButtonBackground(color: Paint) {
        buttonBackground.set(color)
    }
    // 背景色 ----------------------------------------

    // 徽章 ----------------------------------------
    // 徽章可见性 ----------------------------------------
    private val buttonBadgeVisibleMetadata: CssMetaData<KButton, Boolean> =
        object : CssMetaData<KButton, Boolean>("-k-button-badge-visible", StyleConverter.getBooleanConverter(), false) {
            override fun isSettable(styleable: KButton): Boolean {
                return !styleable.buttonBadgeVisible.isBound
            }

            override fun getStyleableProperty(styleable: KButton): StyleableProperty<Boolean> {
                return styleable.buttonBadgeVisible
            }
        }
    private val buttonBadgeVisible =
        SimpleStyleableBooleanProperty(buttonBadgeVisibleMetadata, this@KButton, "buttonBadgeVisible", false)

    fun buttonBadgeVisibleProperty() = buttonBadgeVisible
    fun getButtonBadgeVisible() = buttonBadgeVisible.get()
    fun setButtonBadgeVisible(visible: Boolean) {
        buttonBadgeVisible.set(visible)
    }

    // 徽章半径 ----------------------------------------
    private val buttonBadgeRadiusMetadata: CssMetaData<KButton, Number> = object :
        CssMetaData<KButton, Number>("-k-button-badge-radius", StyleConverter.getSizeConverter(), 8.0) {
        override fun isSettable(styleable: KButton): Boolean {
            return !styleable.buttonBadgeRadius.isBound
        }

        override fun getStyleableProperty(styleable: KButton): StyleableProperty<Number> {
            return styleable.buttonBadgeRadius
        }
    }
    private val buttonBadgeRadius =
        SimpleStyleableObjectProperty(buttonBadgeRadiusMetadata, this@KButton, "buttonBadgeRadius", 8.0)

    fun buttonBadgeRadiusProperty() = buttonBadgeRadius
    fun getButtonBadgeRadius() = buttonBadgeRadius.get()
    fun setButtonBadgeRadius(radius: Double) {
        buttonBadgeRadius.set(radius)
    }

    // 徽章文本 ----------------------------------------
    private val buttonBadgeText = SimpleStringProperty("")
    fun buttonBadgeTextProperty() = buttonBadgeText
    fun getButtonBadgeText() = buttonBadgeText.get()
    fun setButtonBadgeText(text: String) {
        buttonBadgeText.set(text)
    }

    // 徽章文本颜色 ----------------------------------------
    private val buttonBadgeTextFillMetadata: CssMetaData<KButton, Paint> = object :
        CssMetaData<KButton, Paint>("-k-button-badge-text-fill", StyleConverter.getPaintConverter(), Color.WHITE) {
        override fun isSettable(styleable: KButton): Boolean {
            return !styleable.buttonBadgeTextFill.isBound
        }

        override fun getStyleableProperty(styleable: KButton): StyleableProperty<Paint> {
            return styleable.buttonBadgeTextFill
        }
    }
    private val buttonBadgeTextFill =
        SimpleStyleableObjectProperty(buttonBadgeTextFillMetadata, this@KButton, "buttonBadgeTextFill", Color.WHITE)

    fun buttonBadgeTextFillProperty() = buttonBadgeTextFill
    fun getButtonBadgeTextFill() = buttonBadgeTextFill.get()
    fun setButtonBadgeTextFill(color: Paint) {
        buttonBadgeTextFill.set(color)
    }


    // 徽章背景色 ----------------------------------------
    private val buttonBadgeBackgroundMetadata: CssMetaData<KButton, Paint> = object :
        CssMetaData<KButton, Paint>(
            "-k-button-badge-background",
            StyleConverter.getPaintConverter(),
            Paint.valueOf("#d03a52")
        ) {
        override fun isSettable(styleable: KButton): Boolean {
            return !styleable.buttonBadgeBackground.isBound
        }

        override fun getStyleableProperty(styleable: KButton): StyleableProperty<Paint> {
            return styleable.buttonBadgeBackground
        }
    }
    private val buttonBadgeBackground = SimpleStyleableObjectProperty(
        buttonBadgeBackgroundMetadata,
        this@KButton,
        "buttonBadgeBackground",
        Paint.valueOf("#d03a52")
    )

    fun buttonBadgeBackgroundProperty() = buttonBadgeBackground
    fun getButtonBadgeBackground() = buttonBadgeBackground.get()
    fun setButtonBadgeBackground(color: Paint) {
        buttonBadgeBackground.set(color)
    }
    // 徽章 ----------------------------------------

    override fun getControlCssMetaData(): MutableList<CssMetaData<out Styleable, *>> {
        return mutableListOf(
            buttonBackgroundMetadata,
            buttonBadgeVisibleMetadata,
            buttonBadgeRadiusMetadata,
            buttonBadgeTextFillMetadata,
            buttonBadgeBackgroundMetadata
        )
    }

    override fun createDefaultSkin(): Skin<*> = KButtonSkin(this)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getUserAgentStylesheet(): String = javaClass.getResource(STYLESHEET).toExternalForm()

    companion object {
        const val STYLE_CLASS = "k-button"
        const val STYLESHEET = "k-button.css"
    }
}