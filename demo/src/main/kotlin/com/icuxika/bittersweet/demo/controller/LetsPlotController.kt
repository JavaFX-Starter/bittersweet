package com.icuxika.bittersweet.demo.controller

import com.icuxika.bittersweet.demo.annotation.AppFXML
import com.icuxika.bittersweet.demo.component.LayoutAnimator
import com.icuxika.bittersweet.demo.dataset.ChinaAdminDivisionSHPDataset
import com.icuxika.bittersweet.demo.dataset.NaturalEarthDataset
import com.icuxika.bittersweet.dsl.onAction
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingNode
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.geotools.geometry.jts.ReferencedEnvelope
import org.geotools.referencing.crs.DefaultGeographicCRS
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.io.CSVType
import org.jetbrains.kotlinx.dataframe.io.readDelim
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.geom.geomPolygon
import org.jetbrains.letsPlot.geom.geomRect
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.jfx.plot.component.DefaultPlotPanelJfx
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset
import org.jetbrains.letsPlot.tooltips.layerTooltips
import java.awt.Component
import java.awt.Dimension
import java.io.FileInputStream
import java.net.URL
import java.util.*
import javax.swing.JComponent
import javax.swing.SwingUtilities

@AppFXML(fxml = "fxml/lets-plot.fxml")
class LetsPlotController : Initializable {

    @FXML
    private lateinit var rootContainer: StackPane

    @FXML
    private lateinit var container: BorderPane
    private val scope = CoroutineScope(Dispatchers.JavaFx)

    private lateinit var plotsContainer: FlowPane

    private val widthProperty = SimpleDoubleProperty(0.0)
    private val heightProperty = SimpleDoubleProperty(0.0)

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        container.bottom = HBox(
            Label().apply {
                textProperty().bind(SimpleStringProperty("宽度: ").concat(widthProperty.asString()))
            },
            Label().apply {
                textProperty().bind(SimpleStringProperty("高度: ").concat(heightProperty.asString()))
            }
        ).apply {
            alignment = Pos.CENTER
        }
        container.top = HBox(
            Button("打开文件").apply {
                onAction {
                    FileChooser().showOpenDialog(container.scene.window)?.let { file ->
                        scope.launch {
                            plotsContainer.children.add(
                                createPlotSpecsNode {
                                    DataFrame.readDelim(
                                        inStream = FileInputStream(file),
                                        csvType = CSVType.DEFAULT
                                    ).toMap().let { map ->
                                        val p = ggplot(map) + geomPoint(
                                            stat = Stat.count(),
                                        ) {
                                            x = "displ"
                                            y = "hwy"
                                            color = "..count.."
                                            size = "..count.."
                                        }
                                        p.toSpec()
                                    }
                                }.apply {
                                    widthProperty().subscribe { prefWidth ->
                                        prefWidth?.let {
                                            widthProperty.set(it.toDouble())
                                        }
                                    }
                                    heightProperty().subscribe { prefHeight ->
                                        prefHeight?.let {
                                            heightProperty.set(it.toDouble())
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            },
            Button("打开文件2").apply {
                onAction {
                    FileChooser().showOpenDialog(container.scene.window)?.let { file ->
                        scope.launch {
                        }
                    }
                }
            }
        )
        plotsContainer = FlowPane().apply {
            alignment = Pos.CENTER
            hgap = 4.0
            vgap = 4.0
        }

        val scrollPane = ScrollPane().apply {
            prefWidthProperty().bind(container.widthProperty())
            isFitToWidth = true
            content = plotsContainer
        }

        LayoutAnimator().observe(plotsContainer.children)
        plotsContainer.prefWidthProperty().bind(scrollPane.widthProperty())

        container.center = scrollPane

        addChinaAdminDivisionSHP()

        repeat(1) {
            scope.launch {
                plotsContainer.children.add(createPlotSpecsNode {
                    withStrokeAndSpacerLines()
                })
            }
        }
    }

    private fun addChinaAdminDivisionSHP() {
        scope.launch {
            plotsContainer.children.add(createPlotSpecsNode {
                val spatialDataset = ChinaAdminDivisionSHPDataset.country
                val p = letsPlot() + geomPolygon(
                    map = spatialDataset,
                ) {} + ggtitle("中国-国家")
                p.toSpec()
            })
        }
        scope.launch {
            plotsContainer.children.add(createPlotSpecsNode(Dimension(1500, 1000)) {
                val spatialDataset = ChinaAdminDivisionSHPDataset.province
                val p = letsPlot() + geomPolygon(
                    map = spatialDataset,
                ) {
                    fill = "pr_name"
                } + ggtitle("中国-省/直辖市")
                p.toSpec()
            })
        }
    }

    fun addNaturalEarth() {
        scope.launch {
            plotsContainer.children.add(
                createPlotSpecsNode(Dimension(1360, 768)) {
                    val (polygonSpatialDataset, _) = NaturalEarthDataset.naturalEarth110mCulturalVectorsCountries
                    val p = letsPlot() + geomPolygon(
                        map = polygonSpatialDataset,
                        color = "white",
                        tooltips = layerTooltips().line("@{NAME_ZH}")
                    ) {
                        fill = "CONTINENT"
                    } + ggtitle("世界地图-初始")
                    p.toSpec()
                }
            )
        }

        scope.launch {
            plotsContainer.children.add(
                createPlotSpecsNode(Dimension(1360, 768)) {
                    val (polygonSpatialDataset, cnPolygonSpatialDataset) = NaturalEarthDataset.naturalEarth110mCulturalVectorsCountries
                    val p = letsPlot() + geomPolygon(
                        map = polygonSpatialDataset,
                        color = "white",
                        tooltips = layerTooltips().line("@{NAME_ZH}")
                    ) {
                    } + geomPolygon(
                        map = cnPolygonSpatialDataset,
                        color = "#FF6699",
                        fill = "red"
                    ) + ggtitle("世界地图-中国")
                    p.toSpec()
                }
            )
        }

        scope.launch {
            plotsContainer.children.add(
                createPlotSpecsNode(Dimension(1360, 768)) {
                    val (polygonSpatialDataset, _) = NaturalEarthDataset.naturalEarth110mCulturalVectorsCountries
                    val envelope = ReferencedEnvelope(
                        -85.41094255239946, -30.729993455533034,  // longitudes
                        -59.61183, 16.43730316817731,             // latitudes
                        DefaultGeographicCRS.WGS84
                    )
                    val p = letsPlot() + geomPolygon(
                        map = polygonSpatialDataset,
                        color = "white",
                        tooltips = layerTooltips().line("@{NAME_ZH}")
                    ) + geomRect(
                        map = envelope.toSpatialDataset(),
                        size = 4,
                        color = "orange",
                        alpha = 0
                    ) + ggtitle("世界地图-envelope")
                    p.toSpec()
                }
            )
        }

        scope.launch {
            plotsContainer.children.add(
                createPlotSpecsNode(Dimension(1360, 768)) {
                    val (polygonSpatialDataset, _) = NaturalEarthDataset.naturalEarth110mCulturalVectorsCountries
                    val climateData = mapOf(
                        "region" to listOf("Europe", "Asia", "North America", "Africa", "Australia", "Oceania"),
                        "avg_temp" to listOf(8.6, 16.6, 11.7, 21.9, 14.9, 23.9)
                    )
                    val p = letsPlot() + geomPolygon(
                        data = climateData,
                        map = polygonSpatialDataset,
                        mapJoin = "region" to "CONTINENT",
                        color = "white",
                        tooltips = layerTooltips().line("@{NAME_ZH}").line("@{avg_temp}")
                    ) {
                        fill = "avg_temp"
                    } + ggtitle("世界地图-平均温度")
                    p.toSpec()
                }
            )
        }

        scope.launch {
            plotsContainer.children.add(
                createPlotSpecsNode {
                    val citiesSpatialDataset = NaturalEarthDataset.naturalEarth10mCulturalVectorsPopulatedPlaces
                    val p = letsPlot() + geomPoint(
                        data = citiesSpatialDataset,
                        color = "red"
                    ) + ggtitle("人口稠密的地方")
                    p.toSpec()
                }
            )
        }
    }

    private suspend fun createPlotSpecsNode(
        plotSize: Dimension? = null,
        specFunc: suspend () -> MutableMap<String, Any>
    ) =
        withContext(Dispatchers.IO) {
            val spec = specFunc()
            val swingNode = SwingNode().apply {
                SwingUtilities.invokeLater { content = createPlotSpecsDemoWindow(spec, plotSize) }
            }
            StackPane().apply {
                padding = Insets(4.0)
                border = Border(
                    BorderStroke(
                        Color.DODGERBLUE,
                        Color.DODGERBLUE,
                        Color.DODGERBLUE,
                        Color.DODGERBLUE,
                        BorderStrokeStyle.SOLID,
                        BorderStrokeStyle.SOLID,
                        BorderStrokeStyle.SOLID,
                        BorderStrokeStyle.SOLID,
                        CornerRadii(4.0),
                        BorderWidths(2.0, 2.0, 2.0, 2.0),
                        null
                    )
                )
                children.add(swingNode)
            }
        }

    /**
     * https://github.com/JetBrains/lets-plot/blob/master/demo/plot-common/src/commonMain/kotlin/demo/plot/common/model/plotConfig/Pie.kt#L181
     */
    private fun withStrokeAndSpacerLines(): MutableMap<String, Any> {
        val spec = """
        {
          'kind': 'plot',
          'ggtitle': {'text' : 'stroke aes + \'spacer_width\'=2.0 + \'stroke_side\'=\'both\''},
          'theme': { 'line': 'blank', 'axis': 'blank', 'flavor': 'solarized_light' },
          'mapping': { 
            'fill': 'name',
            'slice': 'value',
            'color': 'color',
            'stroke': 'stroke' 
          },
          'layers': [
            {
              'geom': 'pie',
              'stat': 'identity',
              'size': 20,
              'hole': 0.5,
              'spacer_width': 2.0,
              'stroke_side': 'both'
            }
          ],
          'scales': [
            {
              'aesthetic': 'color',
              'discrete': true,
              'scale_mapper_kind': 'color_brewer', 
              'palette': 'Dark2'
            }
          ]
        }""".trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "name" to ('A'..'C').toList(),
            "value" to listOf(50, 30, 60),
            "stroke" to listOf(6, 8, 10),
            "color" to ('a'..'c').toList(),
        )
        return plotSpec
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePlotSpec(spec: String): MutableMap<String, Any> {
        return spec.replace("'", "\"").let {
            JsonSupport.parseJson(it) as MutableMap<String, Any>
        }
    }

    /**
     * https://github.com/JetBrains/lets-plot/blob/master/demo/common-jfx/src/jvmMain/kotlin/demo/common/jfx/demoUtils/PlotObjectsDemoWindowJfx.kt
     */
    fun createPlotObjectsDemoWindow() {}

    /**
     * https://github.com/JetBrains/lets-plot/blob/master/demo/common-jfx/src/jvmMain/kotlin/demo/common/jfx/demoUtils/PlotResizableDemoWindowJfx.kt
     */
    fun createPlotResizableDemoWindow() {}

    /**
     * https://github.com/JetBrains/lets-plot/blob/master/demo/common-jfx/src/jvmMain/kotlin/demo/common/jfx/demoUtils/PlotSpecsDemoWindowJfx.kt
     */
    private fun createPlotSpecsDemoWindow(rawSpec: MutableMap<String, Any>, plotSize: Dimension? = null): JComponent {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val plotPanel = DefaultPlotPanelJfx(
            processedSpec = processedSpec,
            preferredSizeFromPlot = plotSize == null,
            repaintDelay = 300,
            preserveAspectRatio = false,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }

        plotSize?.let {
            plotPanel.preferredSize = it
        }

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        return plotPanel
    }

    /**
     * https://github.com/JetBrains/lets-plot/blob/master/demo/common-jfx/src/jvmMain/kotlin/demo/common/jfx/demoUtils/SvgViewerDemoWindowJfx.kt
     */
    fun createSvgViewerDemoWindow() {}
}