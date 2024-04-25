package com.icuxika.bittersweet.demo.controller

import com.icuxika.bittersweet.demo.AppResource
import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.demo.system.Theme
import com.icuxika.bittersweet.demo.util.FileDownloader
import com.icuxika.bittersweet.dsl.onAction
import com.icuxika.bittersweet.extension.logger
import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXComboBox
import io.github.palexdev.materialfx.controls.MFXProgressBar
import io.github.palexdev.materialfx.controls.MFXTextField
import io.github.palexdev.materialfx.enums.ButtonType
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
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

    private val progressProperty = SimpleDoubleProperty(0.5)
    private val scope = CoroutineScope(Dispatchers.JavaFx)

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        container.center = VBox(
            MFXProgressBar().apply {
                progressProperty().bind(progressProperty)
            },
            MFXComboBox<Theme>().apply {
                items = FXCollections.observableArrayList(Theme.entries)
                selectItem(Theme.LIGHT)
                valueProperty().subscribe { newTheme ->
                    newTheme?.let {
                        AppResource.setTheme(it)
                    }
                }
            },
            MFXButton("下载").apply {
                setPrefSize(120.0, 36.0)
                buttonType = ButtonType.FLAT
                textFill = Color.WHITE
                background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii(4.0), Insets.EMPTY))
                onAction {
                    scope.launch {
                        val fileURL =
                            "https://dldir1.qq.com/qqfile/qq/QQNT/Windows/QQ_9.9.9_240403_x64_01.exe"
                        val filePath =
                            Path.of(System.getProperty("user.home")).resolve("Downloads").resolve("temp")
                                .resolve("result1.exe");
                        FileDownloader.downloadFile(fileURL, filePath).collect {
                            when (it) {
                                is FileDownloader.DownloadState.Downloading -> {
                                    progressProperty.set(it.progress.toDouble())
                                }

                                is FileDownloader.DownloadState.Success -> {
                                    LOGGER.info("下载完成[${Thread.currentThread().name}]-->${it.result}")
                                }

                                is FileDownloader.DownloadState.Error -> {
                                    LOGGER.info("下载失败[${Thread.currentThread().name}]-->${it.throwable.message}")
                                }
                            }
                        }
                    }
                }
            },
            MFXTextField("123"),
            Button("中国智造").apply {
                id = "test-button"
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