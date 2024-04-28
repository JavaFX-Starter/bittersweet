package com.icuxika.bittersweet.demo.dataset

import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.jetbrains.letsPlot.spatial.SpatialDataset
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset

object AliYunDataVDataSet {

    /**
     * https://datav.aliyun.com/portal/school/atlas/area_selector
     * 地名:中华人民共和国
     * adcode: 100000
     */
    val areasV3CN: SpatialDataset by lazy {
        val params = hashMapOf<String, String>()
        params[ShapefileDataStoreFactory.URLP.key] = "file:" + "NaturalEarth/aliyun_datav_100000_cn/aliyun_datav_100000_cn.shp"
        val dataStore = ShapefileDataStoreFactory().createDataStore(params)
        val simpleFeatureCollection = dataStore.getFeatureSource(dataStore.names[0]).features
        val spatialDataset = simpleFeatureCollection.toSpatialDataset()
        spatialDataset
    }
}