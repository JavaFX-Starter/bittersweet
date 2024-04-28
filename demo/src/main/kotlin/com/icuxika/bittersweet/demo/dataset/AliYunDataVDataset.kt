package com.icuxika.bittersweet.demo.dataset

import org.jetbrains.letsPlot.spatial.SpatialDataset
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset

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
}