import com.icuxika.bittersweet.demo.dataset.AliYunDataVDataset
import com.icuxika.bittersweet.demo.dataset.ChinaAdminDivisionSHPDataset
import com.icuxika.bittersweet.demo.dataset.NaturalEarthDataset
import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.filter.text.cql2.CQL
import org.geotools.geojson.geom.GeometryJSON
import org.jetbrains.letsPlot.spatial.SpatialDataset
import org.jetbrains.letsPlot.toolkit.geotools.toSpatialDataset
import kotlin.test.Test

class GeoToolsTest {

    @Test
    fun test() {
        val simpleFeatureCollection = NaturalEarthDataset.readShapeFile("ne_110m_admin_0_countries/ne_110m_admin_0_countries.shp")
        val sub = simpleFeatureCollection.subCollection(CQL.toFilter("CONTINENT NOT LIKE 'Africa'"))
            .subCollection(CQL.toFilter("NAME_ZH = '中华人民共和国'"))
        println(sub.toSpatialDataset()["NAME_ZH"]?.distinct())

        sub.features().let {
            while (it.hasNext()) {
                val simpleFeature = it.next()
                simpleFeature.properties.forEach { property ->
                    println("${property.name}-->${property.type}-->${property.value}")
                }
                val geometry = simpleFeature.defaultGeometry
                println(geometry)
            }
        }
    }

    @Test
    fun read110mCulturalVectorsCountries() {
        val simpleFeatureCollection = NaturalEarthDataset.readShapeFile("ne_110m_admin_0_countries/ne_110m_admin_0_countries.shp")
        val (spatialDataset, _) = NaturalEarthDataset.naturalEarth110mCulturalVectorsCountries
        println(spatialDataset["NAME_ZH"]?.distinct())
        println(spatialDataset["CONTINENT"]?.distinct())
        spatialDataset["CONTINENT"]?.let { continent ->
            continent.groupingBy { it }.eachCount().forEach { (t, u) ->
                println("$t->$u")
            }
        }
        spatialDataset["geometry"]?.let { geometry ->
            println(GeometryJSON().readMultiPolygon(geometry[0]).coordinates.size)
        }

        val subCollection = simpleFeatureCollection.subCollection(CQL.toFilter("CONTINENT = 'Africa'"))
        val africaSpatialDataset = subCollection.toSpatialDataset()

        val jsonString = arrayListOf<String>()
        val indexToDeleteList = arrayListOf<Int>()
        africaSpatialDataset["geometry"]?.let { geometry ->
            geometry.forEachIndexed { index, json ->
                val geometryJSON = GeometryJSON().readMultiPolygon(json)
                val coordinates = geometryJSON.coordinates
                var add = true
                coordinates.find { it.x == 48.9482047585 }?.let {
                    add = false
                    indexToDeleteList.add(index)
                }
                if (add) {
                    jsonString.add(geometryJSON.toString())
                }
            }
        }
        println(indexToDeleteList)

        val africaSpatialDatasetMap = africaSpatialDataset.toMutableMap()
        val newMap = mutableMapOf<String, List<Any?>>()
        for ((key, list) in africaSpatialDatasetMap) {
            val cacheList = mutableListOf<Any?>().apply {
                addAll(list)
            }
            indexToDeleteList.forEachIndexed { index, i ->
                cacheList.removeAt(i - index)
            }
            newMap[key] = cacheList
        }
        SpatialDataset.withGEOJSON(newMap, jsonString)
    }

    @Test
    fun read10mCulturalVectorsPopulatedPlaces() {
        val spatialDataset = NaturalEarthDataset.naturalEarth110mCulturalVectorsBoundaryLines
        println(spatialDataset.keys)
    }

    @Test
    fun read110mCulturalVectorsBoundaryLines() {
        val (spatialDataset, _) = NaturalEarthDataset.naturalEarth110mCulturalVectorsCountries
        spatialDataset["FCLASS_CN"]?.let { data ->
            println(data)
        }
    }

    /**
     * https://datav.aliyun.com/portal/school/atlas/area_selector
     * https://mapshaper.org/
     * https://blog.csdn.net/qq_18298439/article/details/119798641
     *
     * import geopandas
     * data = geopandas.read_file("data.json")
     * data.to_file(driver='ESRI Shapefile', filename="C:/Users/icuxika/Downloads/sp", encoding='utf-8')
     * shpData = geopandas.read_file("C:/Users/icuxika/Downloads/sp/sp.shp")
     * shpData.is_valid
     * shpData.geometry = shpData.geometry.make_valid()
     * shpData.is_valid
     * shpData.to_file(driver='ESRI Shapefile', filename="C:/Users/icuxika/Downloads/sp", encoding='utf-8')
     *
     */
    @Test
    fun readAliYunDataV() {
        val spatialDataset = AliYunDataVDataset.areasV3CN
        println(spatialDataset.keys)
        println(spatialDataset["name"]?.distinct())
    }

    @Test
    fun readChinaAdminDivisionSHPCountry() {
        val spatialDataset = ChinaAdminDivisionSHPDataset.country
        println(spatialDataset.keys)
        println(spatialDataset["cn_name"]?.distinct())
    }

    @Test
    fun readChinaAdminDivisionSHPProvince() {
        val spatialDataset = ChinaAdminDivisionSHPDataset.province
        println(spatialDataset.keys)
        println(spatialDataset["pr_name"]?.distinct())
    }

    @Test
    fun readChinaAdminDivisionSHPCity() {
        val spatialDataset = ChinaAdminDivisionSHPDataset.city
        println(spatialDataset.keys)
    }
}