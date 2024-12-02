package com.icuxika.bittersweet.demo.controller;

import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.dsl.onAction
import javafx.animation.AnimationTimer
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import java.net.URL
import java.util.*
import kotlin.math.roundToLong

@AppFXML(fxml = "fxml/framerate-recorder.fxml")
class FramerateRecorderController : Initializable {
    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane

    // 记录按钮
    @FXML
    private lateinit var recordButton: Button

    // 计算进度条
    @FXML
    private lateinit var calculateProgressBar: ProgressBar

    // 计算总量
    private val calculateTotalDuration = 1.0

    // 透明度递减量
    private val transparencyDecrement = 0.005

    // 计算进度属性
    private val calculateProgress = SimpleDoubleProperty(calculateTotalDuration)

    // 当前帧率属性
    private val currentFramerate: DoubleProperty = SimpleDoubleProperty(0.0)

    // 图表数据序列属性
    private val chartSeriesData =
        SimpleObjectProperty(FXCollections.observableList(mutableListOf<XYChart.Data<Number, Number>>()))

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val framerateTimer = FramerateTimer(transparencyDecrement)

        // 配置记录按钮
        recordButton.apply {
            text = "开始计算"
            disableProperty().bind(
                Bindings.createBooleanBinding(
                    { calculateProgress.value > 0 && calculateProgress.value < 1 },
                    calculateProgress
                )
            )
            onAction { event ->
                if (calculateProgress.value <= 0 || calculateProgress.value >= 1.0) {
                    calculateProgress.value = calculateTotalDuration
                    currentFramerate.value = 0.0
                    chartSeriesData.value.clear()
                    framerateTimer.start()
                }
            }
        }

        // 绑定计算进度条的进度
        calculateProgressBar.progressProperty().bind(SimpleDoubleProperty(1.0).subtract(calculateProgress))

        // 配置帧率图表
        container.center = LineChart<Number, Number>(
            NumberAxis(
                0.0,
                (calculateTotalDuration / transparencyDecrement),
                (calculateTotalDuration / transparencyDecrement) / 10.0
            ),
            NumberAxis(0.0, 200.0, 20.0)
        ).apply {
            animated = false
            createSymbols = false
            xAxis.label = "计数"
            yAxis.label = "FPS (1/s)"
            data.add(XYChart.Series<Number, Number>().apply {
                nameProperty().bind(Bindings.concat("FPS: ", currentFramerate.asString().concat(" 1/s")))
                dataProperty().bind(chartSeriesData)
                chartSeriesData.value.add(XYChart.Data(0, 0))
            })
        }
    }

    /**
     * 计算帧率的内部类
     * @param transparencyDecrement 透明度递减量
     */
    private inner class FramerateTimer(val transparencyDecrement: Double) :
        AnimationTimer() {
        var lastTimestamp = -1L

        /**
         * 处理帧率计算
         * @param now 当前时间戳
         */
        override fun handle(now: Long) {
            if (lastTimestamp <= -1) {
                chartSeriesData.value.clear()
            } else {
                val next = chartSeriesData.value.size
                val diff = now - lastTimestamp
                currentFramerate.value = (1e9 / diff).roundToLong().toDouble()
                chartSeriesData.value.add(XYChart.Data(next, currentFramerate.value))
            }
            lastTimestamp = now

            calculateProgress.value -= transparencyDecrement
            if (calculateProgress.value <= 0) {
                stop()
            }
        }
    }
}

