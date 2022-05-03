package com.icuxika.bittersweet.demo.dsl.data

import com.icuxika.bittersweet.delegate.getProperty
import com.icuxika.bittersweet.delegate.property

class TableViewData {

    var id: Long by property()
    fun idProperty() = getProperty(TableViewData::id)

    var name: String by property()
    fun nameProperty() = getProperty(TableViewData::name)

    var state: State by property()
    fun stateProperty() = getProperty(TableViewData::state)

    enum class State {
        ONLINE, OFFLINE
    }

}