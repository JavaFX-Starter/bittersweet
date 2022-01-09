package com.icuxika.bittersweet

import com.icuxika.bittersweet.control.KButton
import com.icuxika.bittersweet.dsl.*
import com.icuxika.bittersweet.dsl.data.TableViewData
import com.icuxika.bittersweet.extension.logger
import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXTableView
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell
import io.github.palexdev.materialfx.controls.enums.ButtonType
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
import java.util.function.Function

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {

    private val tableViewDataList = FXCollections.observableArrayList(
        TableViewData().apply {
            setId(1L)
            setName("一号")
            setState(TableViewData.State.ONLINE)
        },
        TableViewData().apply {
            setId(2L)
            setName("二号")
            setState(TableViewData.State.OFFLINE)
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
                                MFXTableView(tableViewDataList).apply {
                                    this.tableColumns.addAll(
                                        MFXTableColumn("Id", compareBy(TableViewData::getId)).apply {
                                            rowCellFunction =
                                                Function { data -> MFXTableRowCell(data.idProperty().asString()) }
                                        },
                                        MFXTableColumn("Name", compareBy(TableViewData::getName)).apply {
                                            rowCellFunction = Function { data -> MFXTableRowCell(data.nameProperty()) }
                                        },
                                        MFXTableColumn("State", compareBy(TableViewData::getState)).apply {
                                            rowCellFunction = Function { data ->
                                                MFXTableRowCell(data.stateProperty().asString().concat(" - 点击")).apply {
                                                    graphicTextGap = 4.0
                                                    leadingGraphic = MFXFontIcon("mfx-circle", 6.0).apply {
                                                        colorProperty().bind(Bindings.createObjectBinding({
                                                            if (data.getState() == TableViewData.State.ONLINE) Color.LIMEGREEN
                                                            else Color.SALMON
                                                        }, data.stateProperty()))
                                                    }
                                                    borderProperty().bind(Bindings.createObjectBinding({
                                                        val borderColor =
                                                            if (data.getState() == TableViewData.State.ONLINE) Color.LIMEGREEN
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
                                                    ) { data.setState(if (data.getState() == TableViewData.State.ONLINE) TableViewData.State.OFFLINE else TableViewData.State.ONLINE) }
                                                    padding = Insets(0.0, 4.0, 0.0, 4.0)
                                                }
                                            }
                                        }
                                    )
                                    repeat(10) { index ->
                                        tableViewDataList.add(TableViewData().apply {
                                            setId(index.toLong() + 3)
                                            setName((1000..9999).random().toString())
                                            setState(if (index % 2 == 0) TableViewData.State.ONLINE else TableViewData.State.OFFLINE)
                                        })
                                    }
                                }
                            ),
                            StackPane(
                                VBox(
                                    button<MFXButton> {
                                        text = "MFX"
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
                                            tableViewDataList[0].setName("Two")
                                        }
                                    },
                                    tableView0(tableViewDataList) {
                                        this column tableColumn0<TableViewData, Long>().bindProperty(
                                            SimpleStringProperty("序号"),
                                            TableViewData::getId
                                        ).applyCellFactory {
                                            Pair(null, Button(it.toString()))
                                        }
                                        this column tableColumn0<TableViewData, String>().bindProperty(
                                            SimpleStringProperty("名称"),
                                            TableViewData::getName
                                        ).applyCellFactory {
                                            Pair(null, Label(it))
                                        }
                                        this column tableColumn0<TableViewData, TableViewData.State>().bindProperty(
                                            SimpleStringProperty("名称"),
                                            TableViewData::getState
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

        L.trace("[trace]日志控制台输出");
        L.debug("[debug]日志控制台输出");
        L.info("[info]日志控制台输出");
        L.warn("[warn]日志记录到build/application.log中");
        L.error("[error]日志记录到build/application.log中");
    }

    companion object {
        val L = logger()
    }
}