package com.icuxika.bittersweet.demo.dataset

import org.geotools.filter.text.cql2.CQL
import org.jetbrains.letsPlot.spatial.SpatialDataset
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset

object NaturalEarthDataset: DataSetUtil() {

    override fun basePath(): String {
        return "Dataset/NaturalEarth/"
    }

    val naturalEarth110mCulturalVectorsCountries: Pair<SpatialDataset, SpatialDataset> by lazy {
        val polygonCollection = readShapeFile("ne_110m_admin_0_countries/ne_110m_admin_0_countries.shp")

        val polygonSpatialDataset = polygonCollection.toSpatialDataset(10)

        val cnPolygonCollection = polygonCollection.subCollection(CQL.toFilter("NAME_ZH = '中华人民共和国'"))
        val cnPolygonSpatialDataset = cnPolygonCollection.toSpatialDataset(10)
        Pair(polygonSpatialDataset, cnPolygonSpatialDataset)
    }

    val naturalEarth10mCulturalVectorsPopulatedPlaces: SpatialDataset by lazy {
        readShapeFile("ne_10m_populated_places/ne_10m_populated_places.shp").toSpatialDataset(10)
    }

    val naturalEarth110mCulturalVectorsBoundaryLines: SpatialDataset by lazy {
        readShapeFile("ne_110m_admin_0_boundary_lines_land/ne_110m_admin_0_boundary_lines_land.shp").toSpatialDataset(10)
    }
}