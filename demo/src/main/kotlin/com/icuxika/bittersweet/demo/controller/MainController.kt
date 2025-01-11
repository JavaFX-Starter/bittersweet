package com.icuxika.bittersweet.demo.controller

import com.icuxika.bittersweet.control.KButton
import com.icuxika.bittersweet.demo.ApiRegistrar
import com.icuxika.bittersweet.demo.AppResource
import com.icuxika.bittersweet.demo.AppView
import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.demo.api.*
import com.icuxika.bittersweet.demo.model.User
import com.icuxika.bittersweet.demo.remote.UserClient
import com.icuxika.bittersweet.demo.system.SystemProperties
import com.icuxika.bittersweet.demo.system.Theme
import com.icuxika.bittersweet.demo.util.FileDownloader
import com.icuxika.bittersweet.dsl.onAction
import com.icuxika.bittersweet.extension.logger
import javafx.beans.binding.When
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.javafx.JavaFx
import java.net.URL
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@AppFXML(fxml = "fxml/main.fxml")
class MainController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    private val scope = CoroutineScope(Dispatchers.JavaFx)

    private lateinit var job: Job
    private val downloadUrl = "https://dldir1.qq.com/qqfile/qq/QQNT/Windows/QQ_9.9.9_240403_x64_01.exe"
    private val savePath = Path.of(System.getProperty("user.home")).resolve("Downloads").resolve("temp")
        .resolve("QQ.exe")

    private val progressProperty = SimpleDoubleProperty(ProgressIndicator.INDETERMINATE_PROGRESS)
    private val cButtonBadgeVisibleProperty = SimpleBooleanProperty(true)

    private suspend fun Flow<ProgressFlowState<Double>>.resolveFlowCollector() {
        collect {
            when (it) {
                is ProgressFlowState.Progress -> {
                    progressProperty.set(it.progress)
                }

                is ProgressFlowState.Success -> {
                    LOGGER.info("下载完成[${Thread.currentThread().name}]-->${it.result}")
                    LOGGER.info("文件保存路径->$savePath")
                }

                is ProgressFlowState.Error -> {
                    LOGGER.info("下载失败[${Thread.currentThread().name}]-->${it.throwable.message}")
                }
            }
        }
    }

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
            HBox(
                Button("下载1").apply {
                    styleClass.add("test-button")
                    onAction {
                        job = scope.launch(CoroutineExceptionHandler { _, exception ->
                            println("下载1 捕获到异常 ${exception.message}")
                        }) {
                            FileDownloader.downloadFile(downloadUrl, savePath).resolveFlowCollector()
                        }
                    }
                },
                Button("下载2").apply {
                    styleClass.add("test-button")
                    onAction {
                        job = scope.launch(CoroutineExceptionHandler { _, exception ->
                            println("下载2 捕获到异常 ${exception.message}")
                        }) {
                            suspendGetFileFlow(downloadUrl, savePath).resolveFlowCollector()
                        }
                    }
                },
                Button("取消").apply {
                    styleClass.add("test-button")
                    onAction {
                        scope.launch(CoroutineExceptionHandler { _, exception ->
                            println("取消 捕获到异常 ${exception.message}")
                        }) {
                            job.cancelAndJoin()
                            progressProperty.set(ProgressIndicator.INDETERMINATE_PROGRESS)
                            savePath.toFile().delete()
                        }
                    }
                },
            ).apply {
                alignment = Pos.CENTER
                spacing = 12.0
            },
            Text().apply {
                textProperty().bind(
                    SimpleStringProperty("进度: ").concat(
                        When(
                            progressProperty.isEqualTo(
                                SimpleDoubleProperty(ProgressIndicator.INDETERMINATE_PROGRESS)
                            )
                        ).then(SimpleStringProperty("0"))
                            .otherwise(progressProperty.multiply(100).asString("%.2f"))
                    ).concat("%")
                )
            },
            HBox(
                Button("上传").apply {
                    onAction {
                        FileChooser().apply {
                            title = "选择大文件"
                        }.showOpenDialog(container.scene.window)?.let { selectedFile ->
                            job = scope.launch {
                                suspendPostFileFlow<ApiData<Unit>>(
                                    "${SystemProperties.serverUrl}/users/upload", mapOf(
                                        Api.REQUEST_KEY_FILE to selectedFile,
                                        "id" to "11",
                                    )
                                ).collect {
                                    when (it) {
                                        is ProgressFlowState.Progress -> {
                                            progressProperty.set(it.progress)
                                        }

                                        is ProgressFlowState.Error -> {
                                            LOGGER.info("上传失败[${Thread.currentThread().name}]-->${it.throwable.message}")
                                        }

                                        is ProgressFlowState.Success -> {
                                            println(it.result)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            ).apply {
                alignment = Pos.CENTER
                spacing = 12.0
            },
            Button("调用UserClient").apply {
                styleClass.add("test-button")
                onAction {
                    println(
                        userClient.getUser(
                            1,
                            "client-get",
                            LocalDate.of(2022, 12, 12),
                            LocalTime.of(11, 11),
                            LocalDateTime.of(2022, 12, 12, 11, 11)
                        )
                    )
                    println(
                        userClient.postUser(
                            User(
                                1,
                                "client-post",
                                LocalDate.of(2022, 12, 12),
                                LocalTime.of(11, 11),
                                LocalDateTime.of(2022, 12, 12, 11, 11)
                            )
                        )
                    )
                }
            },
            Button("启动画面").apply {
                styleClass.add("test-button")
                onAction {
                    AppView(SplashScreenController::class).show()
                    (container.scene.window as Stage).close()
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
        val userClient = ApiRegistrar.getProxy<UserClient>(UserClient::class)
    }
}