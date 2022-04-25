package com.icuxika.bittersweet.demo.dsl

import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import kotlin.reflect.KFunction1

internal typealias Initializer<T> = T.() -> Unit

inline fun <reified T> constructAndInitialize(initializer: Initializer<T> = {}): T =
    T::class.java.getDeclaredConstructor().newInstance().apply(initializer)

inline fun <reified T : Accordion> accordion(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Button> button(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : ButtonBar> buttonBar(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun ButtonBase.onAction(crossinline block: (ActionEvent) -> Unit) {
    this.onAction = EventHandler { block(it) }
}

inline fun <reified T : CheckBox> checkBox(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <T> checkBoxTreeItem0(initializer: CheckBoxTreeItem<T>.() -> Unit = {}): CheckBoxTreeItem<T> =
    CheckBoxTreeItem<T>().apply(initializer)

inline fun <reified T : CheckBoxTreeItem<U>, U> checkBoxTreeItem1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : CheckBoxTreeItem<*>> checkBoxTreeItem(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : CheckMenuItem> checkMenuItem(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> choiceBox0(initializer: ChoiceBox<T>.() -> Unit = {}): ChoiceBox<T> = ChoiceBox<T>().apply(initializer)

inline fun <reified T : ChoiceBox<U>, U> choiceBox1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ChoiceBox<*>> choiceBox(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> choiceDialog0(initializer: ChoiceDialog<T>.() -> Unit = {}): ChoiceDialog<T> =
    ChoiceDialog<T>().apply(initializer)

inline fun <reified T : ChoiceDialog<U>, U> choiceDialog1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ChoiceDialog<*>> choiceDialog(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ColorPicker> colorPicker(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> comboBox0(initializer: ComboBox<T>.() -> Unit = {}): ComboBox<T> = ComboBox<T>().apply(initializer)

inline fun <reified T : ComboBox<U>, U> comboBox1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ComboBox<*>> comboBox(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : ContextMenu> contextMenu(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : Control> control(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : CustomMenuItem> customMenuItem(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : DateCell> dateCell(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : DatePicker> datePicker(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> dialog0(initializer: Dialog<T>.() -> Unit = {}): Dialog<T> = Dialog<T>().apply(initializer)

inline fun <reified T : Dialog<U>, U> dialog1(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Dialog<*>> dialog(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : DialogPane> dialogPane(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : Hyperlink> hyperlink(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Label> label(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <T> listCell0(initializer: ListCell<T>.() -> Unit = {}): ListCell<T> = ListCell<T>().apply(initializer)

inline fun <reified T : ListCell<U>, U> listCell1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ListCell<*>> listCell(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <T> listView0(initializer: ListView<T>.() -> Unit = {}): ListView<T> = ListView<T>().apply(initializer)

inline fun <reified T : ListView<U>, U> listView1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ListView<*>> listView(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Menu> menu(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : MenuBar> menuBar(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : MenuButton> menuButton(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : MenuItem> menuItem(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Pagination> pagination(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : PasswordField> passwordField(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : PopupControl> popupControl(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ProgressBar> progressBar(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ProgressIndicator> progressIndicator(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : RadioButton> radioButton(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : RadioMenuItem> radioMenuItem(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ScrollBar> scrollBar(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : ScrollPane> scrollPane(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : Separator> separator(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : SeparatorMenuItem> separatorMenuItem(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : Slider> slider(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <T> spinner0(initializer: Spinner<T>.() -> Unit = {}): Spinner<T> = Spinner<T>().apply(initializer)

inline fun <reified T : Spinner<U>, U> spinner1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : Spinner<*>> spinner(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : SplitMenuButton> splitMenuButton(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : SplitPane> splitPane(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Tab> tab(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <U, V> tableCell0(initializer: TableCell<U, V>.() -> Unit = {}): TableCell<U, V> =
    TableCell<U, V>().apply(initializer)

inline fun <reified T : TableCell<U, V>, U, V> tableCell1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TableCell<*, *>> tableCell(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <U, V> tableColumn0(initializer: TableColumn<U, V>.() -> Unit = {}): TableColumn<U, V> =
    TableColumn<U, V>().apply(initializer)

inline fun <U, V> tableColumn0(text: String, initializer: TableColumn<U, V>.() -> Unit = {}): TableColumn<U, V> =
    TableColumn<U, V>(text).apply(initializer)

inline fun <reified T : TableColumn<*, U>, U> tableColumn1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TableColumn<*, U>, U> tableColumn1(text: String, initializer: Initializer<T> = {}): T =
    T::class.java.getDeclaredConstructor(String::class.java).newInstance(text).apply(initializer)

inline fun <reified T : TableColumn<U, V>, U, V> tableColumn2(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TableColumn<U, V>, U, V> tableColumn2(text: String, initializer: Initializer<T> = {}): T =
    T::class.java.getDeclaredConstructor(String::class.java).newInstance(text).apply(initializer)

inline fun <reified T : TableColumn<*, *>> tableColumn(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> tableView0(initializer: TableView<T>.() -> Unit = {}): TableView<T> = TableView<T>().apply(initializer)
inline fun <T> tableView0(items: ObservableList<T>, initializer: TableView<T>.() -> Unit = {}): TableView<T> =
    TableView(items).apply(initializer)

inline fun <reified T : TableView<U>, U> tableView1(items: ObservableList<U>, initializer: Initializer<T> = {}): T =
    T::class.java.getDeclaredConstructor(ObservableList::class.java).newInstance(items).apply(initializer)

inline fun <reified T : TableView<U>, U> tableView1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TableView<*>> tableView(items: ObservableList<*>, initializer: Initializer<T> = {}): T =
    T::class.java.getDeclaredConstructor(ObservableList::class.java).newInstance(items).apply(initializer)

inline fun <reified T : TableView<*>> tableView(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TabPane> tabPane(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : TextArea> textArea(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : TextField> textField(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : TitledPane> titledPane(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ToggleButton> toggleButton(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ToggleGroup> toggleGroup(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : ToolBar> toolBar(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <reified T : Tooltip> tooltip(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <T> treeCell0(initializer: TreeCell<T>.() -> Unit = {}): TreeCell<T> = TreeCell<T>().apply(initializer)

inline fun <reified T : TreeCell<U>, U> treeCell1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TreeCell<*>> treeCell(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <T> treeItem0(initializer: TreeItem<T>.() -> Unit = {}): TreeItem<T> = TreeItem<T>().apply(initializer)

inline fun <reified T : TreeItem<U>, U> treeItem1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TreeItem<*>> treeItem(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

inline fun <U, V> treeTableCell0(initializer: TreeTableCell<U, V>.() -> Unit = {}): TreeTableCell<U, V> =
    TreeTableCell<U, V>().apply(initializer)

inline fun <reified T : TreeTableCell<U, V>, U, V> treeTableCell1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TreeTableCell<*, *>> treeTableCell(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <U, V> treeTableColumn0(initializer: TreeTableColumn<U, V>.() -> Unit = {}): TreeTableColumn<U, V> =
    TreeTableColumn<U, V>().apply(initializer)

inline fun <reified T : TreeTableColumn<U, V>, U, V> treeTableColumn1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TreeTableColumn<*, *>> treeTableColumn(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> treeTableView0(initializer: TreeTableView<T>.() -> Unit = {}): TreeTableView<T> =
    TreeTableView<T>().apply(initializer)

inline fun <reified T : TreeTableView<U>, U> treeTableView1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TreeTableView<*>> treeTableView(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <T> treeView0(initializer: TreeView<T>.() -> Unit = {}): TreeView<T> = TreeView<T>().apply(initializer)

inline fun <reified T : TreeView<U>, U> treeView1(initializer: Initializer<T> = {}): T =
    constructAndInitialize(initializer)

inline fun <reified T : TreeView<*>> treeView(initializer: Initializer<T> = {}): T = constructAndInitialize(initializer)

// TableView
inline infix fun <reified T : TableView<U>, U> T.column(value: TableColumn<U, *>) {
    this.columns.add(value)
}

inline fun <reified T : TableColumn<U, V>, U, V> T.bindProperty(
    propertyGetFunc: KFunction1<U, V>
) = this.apply {
    this.cellValueFactory = PropertyValueFactory(propertyGetFunc.name.lowercase().replace("get", ""))
}

inline fun <reified T : TableColumn<U, V>, U, V> T.bindProperty(
    textProperty: StringProperty,
    propertyGetFunc: KFunction1<U, V>
) = this.apply {
    this.textProperty().bind(textProperty)
    this.cellValueFactory = PropertyValueFactory(propertyGetFunc.name.lowercase().replace("get", ""))
}

fun <T : TableColumn<U, V>, U, V> T.applyCellFactory(block: (item: V) -> Pair<String?, Node?>) = this.apply {
    this.cellFactory = Callback<TableColumn<U, V>, TableCell<U, V>> {
        object : TableCell<U, V>() {
            override fun updateItem(item: V, empty: Boolean) {
                super.updateItem(item, empty)
                if (empty || item == null) {
                    text = null
                    graphic = null
                } else {
                    val (string, node) = block(item)
                    text = string
                    graphic = node
                }
            }
        }
    }
}
