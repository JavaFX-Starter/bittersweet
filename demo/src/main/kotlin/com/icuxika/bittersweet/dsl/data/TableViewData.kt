package com.icuxika.bittersweet.dsl.data

import io.github.palexdev.materialfx.filter.IFilterable
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class TableViewData : IFilterable {

    private val id: SimpleLongProperty = SimpleLongProperty()
    fun idProperty(): SimpleLongProperty = id
    fun setId(value: Long) = idProperty().set(value)
    fun getId(): Long = idProperty().get()

    private val name: SimpleStringProperty = SimpleStringProperty()
    fun nameProperty(): SimpleStringProperty = name
    fun setName(value: String) = nameProperty().set(value)
    fun getName(): String = nameProperty().get()

    private val state: SimpleObjectProperty<State> = SimpleObjectProperty()
    fun stateProperty(): SimpleObjectProperty<State> = state
    fun setState(value: State) = stateProperty().set(value)
    fun getState(): State = stateProperty().get()

    override fun toFilterString() = getId().toString() + " " + getName()

    enum class State {
        ONLINE, OFFLINE
    }
}