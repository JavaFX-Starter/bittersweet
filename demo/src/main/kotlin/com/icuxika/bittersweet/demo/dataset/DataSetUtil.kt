package com.icuxika.bittersweet.demo.dataset

import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.simple.SimpleFeatureCollection

abstract class DataSetUtil {

    abstract fun basePath(): String

    fun readShapeFile(path: String): SimpleFeatureCollection {
        val params = hashMapOf<String, String>()
        params[ShapefileDataStoreFactory.URLP.key] = "file:${basePath()}$path"
        val dataStore = ShapefileDataStoreFactory().createDataStore(params)
        val simpleFeatureCollection = dataStore.getFeatureSource(dataStore.names[0]).features
        return simpleFeatureCollection
    }

}