package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.demo.util.FileDownloader
import com.icuxika.bittersweet.dsl.onAction
import com.icuxika.bittersweet.extension.logger
import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXProgressBar
import io.github.palexdev.materialfx.controls.MFXTextField
import io.github.palexdev.materialfx.enums.ButtonType
import javafx.application.Application
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {

    private val scope = CoroutineScope(Dispatchers.JavaFx)

    private val progressProperty = SimpleDoubleProperty(0.5)

    override fun start(primaryStage: Stage?) {
        primaryStage?.apply {
            scene = Scene(
                VBox(
                    MFXProgressBar().apply {
                        progressProperty().bind(progressProperty)
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
                                            L.info("下载完成[${Thread.currentThread().name}]-->${it.result}")
                                        }

                                        is FileDownloader.DownloadState.Error -> {
                                            L.info("下载失败[${Thread.currentThread().name}]-->${it.throwable.message}")
                                        }
                                    }
                                }
                            }
                        }
                    },
                    MFXTextField("123")
                ).apply {
                    alignment = Pos.CENTER
                    spacing = 16.0
                },
                800.0, 600.0
            )
        }?.show()

        L.trace("[trace]日志控制台输出")
        L.debug("[debug]日志控制台输出")
        L.info("[info]日志记录到logs/application.log中")
        L.warn("[warn]日志记录到logs/application.log中")
        L.error("[error]日志记录到logs/application.log中")

        javaClass.getResourceAsStream("/application.properties").use {
            Properties().apply { load(it) }
        }.let { properties ->
            L.info("当前环境：${properties["environment"]}")
        }
    }

    companion object {
        val L = logger()
    }
}