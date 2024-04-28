package com.icuxika.bittersweet.demo.dataset

import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.filter.text.cql2.CQL
import org.jetbrains.letsPlot.spatial.SpatialDataset
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset

object NaturalEarthDataSet {

    val naturalEarth110mCulturalVectorsCountries: Pair<SpatialDataset, SpatialDataset> by lazy {
        val polygonParams = hashMapOf<String, String>()
        polygonParams[ShapefileDataStoreFactory.URLP.key] =
            "file:NaturalEarth/ne_110m_admin_0_countries/ne_110m_admin_0_countries.shp"
        val polygonDataStore = ShapefileDataStoreFactory().createDataStore(polygonParams)

        val polygonCollection = polygonDataStore.getFeatureSource(polygonDataStore.names[0]).features

        val polygonSpatialDataset = polygonCollection.toSpatialDataset(10)

        val cnPolygonCollection = polygonCollection.subCollection(CQL.toFilter("NAME_ZH = '中华人民共和国'"))
        val cnPolygonSpatialDataset = cnPolygonCollection.toSpatialDataset(10)
        Pair(polygonSpatialDataset, cnPolygonSpatialDataset)
    }

    val naturalEarth10mCulturalVectorsPopulatedPlaces: SpatialDataset by lazy {
        val citiesParams = hashMapOf<String, String>()
        citiesParams[ShapefileDataStoreFactory.URLP.key] =
            "file:NaturalEarth/ne_10m_populated_places/ne_10m_populated_places.shp"
        val citiesDataStore = ShapefileDataStoreFactory().createDataStore(citiesParams)
        val citiesCollection = citiesDataStore.getFeatureSource(citiesDataStore.names[0]).features
        val citiesSpatialDataset = citiesCollection.toSpatialDataset()
        citiesSpatialDataset
    }
}