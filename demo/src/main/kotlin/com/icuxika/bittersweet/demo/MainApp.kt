package com.icuxika.bittersweet.demo

import com.icuxika.bittersweet.control.KButton
import com.icuxika.bittersweet.delegate.getProperty
import com.icuxika.bittersweet.delegate.property
import com.icuxika.bittersweet.demo.dsl.data.TableViewData
import com.icuxika.bittersweet.dsl.*
import com.icuxika.bittersweet.extension.logger
import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView
import io.github.palexdev.materialfx.controls.MFXTableColumn
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell
import io.github.palexdev.materialfx.enums.ButtonType
import io.github.palexdev.materialfx.filter.EnumFilter
import io.github.palexdev.materialfx.filter.LongFilter
import io.github.palexdev.materialfx.filter.StringFilter
import io.github.palexdev.materialfx.font.MFXFontIcon
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.util.*
import java.util.function.Function

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {

    lateinit var mfxPaginatedTableView: MFXPaginatedTableView<TableViewData>

    private var buttonText by property("Button")
    private fun buttonTextProperty() = getProperty(MainApp::buttonText)

    private val tableViewDataList = FXCollections.observableArrayList(
        TableViewData().apply {
            id = 1L
            name = "一号"
            state = TableViewData.State.ONLINE
        },
        TableViewData().apply {
            id = 2L
            name = "二号"
            state = TableViewData.State.OFFLINE
        }
    )

    override fun start(primaryStage: Stage?) {
        primaryStage?.apply {
            scene = Scene(
                tabPane {
                    tabs.add(tab {
                        text = "表格"
                        content = SplitPane(
                            StackPane(
                                button<KButton> {
                                    setPrefSize(248.0, 84.0)
                                }
                            ),
                            StackPane(
                                MFXPaginatedTableView(tableViewDataList).apply {
                                    this.tableColumns.addAll(
                                        MFXTableColumn("Id", compareBy(TableViewData::id)).apply {
                                            rowCellFactory =
                                                Function { MFXTableRowCell(TableViewData::id) }
                                        },
                                        MFXTableColumn("Name", compareBy(TableViewData::name)).apply {
                                            rowCellFactory = Function { MFXTableRowCell(TableViewData::name) }
                                        },
                                        MFXTableColumn("State", compareBy(TableViewData::state)).apply {
                                            rowCellFactory = Function { data ->
                                                MFXTableRowCell(TableViewData::state).apply {
                                                    graphicTextGap = 4.0
                                                    leadingGraphic = MFXFontIcon("mfx-circle", 6.0).apply {
                                                        colorProperty().bind(Bindings.createObjectBinding({
                                                            if (data.state == TableViewData.State.ONLINE) Color.LIMEGREEN
                                                            else Color.SALMON
                                                        }, data.stateProperty()))
                                                    }
                                                    borderProperty().bind(Bindings.createObjectBinding({
                                                        val borderColor =
                                                            if (data.state == TableViewData.State.ONLINE) Color.LIMEGREEN
                                                            else Color.SALMON
                                                        Border(
                                                            BorderStroke(
                                                                borderColor,
                                                                BorderStrokeStyle.SOLID,
                                                                CornerRadii(4.0),
                                                                BorderWidths(1.0)
                                                            )
                                                        )
                                                    }, data.stateProperty()))
                                                    addEventFilter(
                                                        MouseEvent.MOUSE_PRESSED
                                                    ) {
                                                        data.state =
                                                            (if (data.state == TableViewData.State.ONLINE) TableViewData.State.OFFLINE else TableViewData.State.ONLINE)
                                                    }
                                                    padding = Insets(0.0, 4.0, 0.0, 4.0)
                                                }
                                            }
                                        }
                                    )
                                    filters.addAll(
                                        LongFilter("Id", TableViewData::id),
                                        StringFilter("Name", TableViewData::name),
                                        EnumFilter("State", TableViewData::state, TableViewData.State::class.java)
                                    )
                                    repeat(10) { index ->
                                        tableViewDataList.add(TableViewData().apply {
                                            id = index.toLong() + 3
                                            name = (1000..9999).random().toString()
                                            state =
                                                if (index % 2 == 0) TableViewData.State.ONLINE else TableViewData.State.OFFLINE
                                        })
                                    }

                                    mfxPaginatedTableView = this
                                }
                            ),
                            StackPane(
                                VBox(
                                    button<MFXButton> {
                                        textProperty().bind(buttonTextProperty())
                                        buttonType = ButtonType.RAISED
                                        textFill = Color.WHITE
                                        background =
                                            Background(
                                                BackgroundFill(
                                                    Color.DODGERBLUE,
                                                    CornerRadii.EMPTY,
                                                    Insets.EMPTY
                                                )
                                            )
                                        setPrefSize(84.0, 24.0)

                                        onAction {
                                            tableViewDataList[0].name = "Two"
                                            buttonText = "按钮"
                                            mfxPaginatedTableView.update()
                                        }
                                    },
                                    tableView0(tableViewDataList) {
                                        this column tableColumn0<TableViewData, Long>().bindProperty(
                                            SimpleStringProperty("序号"),
                                            TableViewData::id
                                        ).applyCellFactory {
                                            Pair(null, Button(it.toString()))
                                        }
                                        this column tableColumn0<TableViewData, String>().bindProperty(
                                            SimpleStringProperty("名称"),
                                            TableViewData::name
                                        ).applyCellFactory {
                                            Pair(null, Label(it))
                                        }
                                        this column tableColumn0<TableViewData, TableViewData.State>().bindProperty(
                                            SimpleStringProperty("状态"),
                                            TableViewData::state
                                        ).applyCellFactory {
                                            Pair(null, Label(it.toString()))
                                        }
                                    }
                                )
                            )
                        )
                    })
                },
                800.0, 600.0
            )
        }?.show()

        L.trace("[trace]日志控制台输出")
        L.debug("[debug]日志控制台输出")
        L.info("[info]日志控制台输出")
        L.warn("[warn]日志记录到build/application.log中")
        L.error("[error]日志记录到build/application.log中")

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