package com.icuxika.bittersweet.demo.dataset

import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset

object ChinaAdminDivisionSHPDataset: DataSetUtil() {

    override fun basePath(): String {
        return "Dataset/ChinaAdminDivisionSHP/"
    }

    val country by lazy {
        readShapeFile("Country/country.shp").toSpatialDataset(10)
    }
    val province by lazy {
        readShapeFile("Province/province.shp").toSpatialDataset(10)
    }
    val city by lazy {
        readShapeFile("City/city.shp").toSpatialDataset(10)
    }
    val district by lazy {
        readShapeFile("District/district.shp").toSpatialDataset(10)
    }
}