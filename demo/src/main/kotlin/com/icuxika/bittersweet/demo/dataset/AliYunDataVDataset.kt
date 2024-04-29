package com.icuxika.bittersweet.demo.dataset

import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.api.feature.simple.SimpleFeatureType
import org.geotools.api.feature.type.GeometryDescriptor
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geojson.geom.GeometryJSON
import org.jetbrains.letsPlot.geom.geomPolygon
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.spatial.SpatialDataset
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.util.GeometryFixer
import java.io.File

object AliYunDataVDataset: DataSetUtil() {

    override fun basePath(): String {
        return "Dataset/AliYunDataV/"
    }

    /**
     * https://datav.aliyun.com/portal/school/atlas/area_selector
     * 地名:中华人民共和国
     * adcode: 100000
     */
    val areasV3CN: SpatialDataset by lazy {
        readShapeFile("aliyun_datav_100000_cn/aliyun_datav_100000_cn.shp").toSpatialDataset()
    }

    val areasV3CNFromJson: SpatialDataset by lazy {
        File("${basePath()}scripts/中华人民共和国.json").let { file ->
            FeatureJSON(GeometryJSON(6)).readFeatureCollection(file).let { featureCollection ->
                val attributeDescriptors = (featureCollection.schema as SimpleFeatureType).attributeDescriptors
                val geometryAttribute = attributeDescriptors?.find { it is GeometryDescriptor }
                    ?: throw IllegalArgumentException("No geometry attribute")
                featureCollection.features().use {
                    while (it.hasNext()) {
                        val feature = it.next()
                        require(feature is SimpleFeature)
                        val featureGeometry = feature.getAttribute(geometryAttribute.name)
                        require(featureGeometry is Geometry)
                        if (!featureGeometry.isValid) {
                            val fixedGeometry = GeometryFixer.fix(featureGeometry)
                            feature.setAttribute(geometryAttribute.name, fixedGeometry)
                        }
                    }
                }
                featureCollection.toSpatialDataset(10)
            }
        }
    }
}