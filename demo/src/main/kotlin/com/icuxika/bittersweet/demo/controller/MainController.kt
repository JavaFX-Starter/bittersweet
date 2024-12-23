package com.icuxika.bittersweet.demo.controller

import com.icuxika.bittersweet.control.KButton
import com.icuxika.bittersweet.demo.AppResource
import com.icuxika.bittersweet.demo.AppView
import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.demo.api.ProgressFlowState
import com.icuxika.bittersweet.demo.system.Theme
import com.icuxika.bittersweet.demo.util.FileDownloader
import com.icuxika.bittersweet.dsl.onAction
import com.icuxika.bittersweet.extension.logger
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Callback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.net.URL
import java.nio.file.Path
import java.util.*

@AppFXML(fxml = "fxml/main.fxml")
class MainController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    private val scope = CoroutineScope(Dispatchers.JavaFx)

    private val progressProperty = SimpleDoubleProperty(ProgressIndicator.INDETERMINATE_PROGRESS)
    private val cButtonBadgeVisibleProperty = SimpleBooleanProperty(true)

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        container.sceneProperty().addListener { _, oldScene, newScene ->
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener { _, oldWindow, newWindow ->
                    if (oldWindow == null && newWindow != null) {
                        // stage initialized
                    }
                }
            }
        }

        container.center = VBox(
            ProgressBar().apply {
                progressProperty().bind(progressProperty)
            },
            Button("下载").apply {
                styleClass.add("test-button")
                onAction {
                    scope.launch {
                        val fileURL =
                            "https://dldir1.qq.com/qqfile/qq/QQNT/Windows/QQ_9.9.9_240403_x64_01.exe"
                        val filePath =
                            Path.of(System.getProperty("user.home")).resolve("Downloads").resolve("temp")
                                .resolve("result1.exe");
                        FileDownloader.downloadFile(fileURL, filePath).collect {
                            when (it) {
                                is ProgressFlowState.Progress -> {
                                    progressProperty.set(it.progress)
                                }

                                is ProgressFlowState.Success -> {
                                    LOGGER.info("下载完成[${Thread.currentThread().name}]-->${it.result}")
                                    LOGGER.info("文件保存路径->$filePath")
                                }

                                is ProgressFlowState.Error -> {
                                    LOGGER.info("下载失败[${Thread.currentThread().name}]-->${it.throwable.message}")
                                }
                            }
                        }
                    }
                }
            },
            ComboBox(FXCollections.observableArrayList(Theme.entries)).apply {
                valueProperty().bindBidirectional(AppResource.themeProperty())
                cellFactory = Callback<ListView<Theme>, ListCell<Theme>> {
                    object : ListCell<Theme>() {
                        override fun updateItem(item: Theme?, empty: Boolean) {
                            super.updateItem(item, empty)
                            graphic = if (item == null || empty) {
                                null
                            } else {
                                Label().apply {
                                    textFill = Color.BLACK
                                    textProperty().bind(AppResource.getLanguageBinding(item.value))
                                }
                            }
                        }
                    }
                }
                buttonCell = cellFactory.call(null)
            },
            ComboBox(FXCollections.observableArrayList(AppResource.SUPPORT_LANGUAGE_LIST)).apply {
                valueProperty().subscribe { newLocale ->
                    newLocale?.let {
                        AppResource.setLanguage(it)
                    }
                }
                valueProperty().bindBidirectional(AppResource.localeProperty())
                cellFactory = Callback<ListView<Locale>, ListCell<Locale>> {
                    object : ListCell<Locale>() {
                        override fun updateItem(item: Locale?, empty: Boolean) {
                            super.updateItem(item, empty)
                            graphic = if (item == null || empty) {
                                null
                            } else {
                                val key = when (item) {
                                    Locale.SIMPLIFIED_CHINESE -> "lang_zh_CN"
                                    Locale.ENGLISH -> "lang_en"
                                    else -> throw IllegalArgumentException("不支持的语言[${item.displayName}]")
                                }
                                Label().apply {
                                    textFill = Color.BLACK
                                    textProperty().bind(AppResource.getLanguageBinding(key))
                                }
                            }
                        }
                    }
                }
                buttonCell = cellFactory.call(null)
            },
            Button("图表").apply {
                styleClass.add("test-button")
                onAction {
                    val letsPlotView = AppView(LetsPlotController::class)
                    letsPlotView.show()
                    (container.scene.window as Stage).close()
                }
            },
            KButton("动感光波", 128.0, 32.0).apply {
                styleClass.add("k-button-primary")
                textFill = Color.WHITE
                onAction {
                    cButtonBadgeVisibleProperty.set(!cButtonBadgeVisibleProperty.get())
                }
            },
            KButton("帧率图表", 128.0, 32.0).apply {
                styleClass.add("k-button-primary")
                textFill = Color.WHITE
                buttonBadgeVisibleProperty().bind(cButtonBadgeVisibleProperty)
                setButtonBadgeText("77")
                onAction { event ->
                    val framerateRecorderView = AppView(FramerateRecorderController::class)
                    framerateRecorderView.show()
                    (container.scene.window as Stage).close()
                }
            },
            KButton("FXGL", 128.0, 32.0).apply {
                styleClass.add("k-button-primary")
                textFill = Color.WHITE
                buttonBadgeVisibleProperty().bind(cButtonBadgeVisibleProperty)
                setButtonBadgeText("77")
                onAction { event ->
                    val fxglView = AppView(FXGLController::class)
                    fxglView.show()
                    (container.scene.window as Stage).close()
                }
            }
        ).apply {
            alignment = Pos.CENTER
            spacing = 16.0
        }
    }

    companion object {
        val LOGGER = logger()
    }
}