# [Natural Earth](https://www.naturalearthdata.com/)
Natural Earth 是一个提供 1:10、1:50 和 1:110 百万比例尺公共领域地图数据集

## `Downloads - 1:10m Cultural Vectors - Populated Places`
- `ne_10m_populated_places` 城市和城镇点

> [Download populated places (2.68 MB) version 5.1.2](https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/ne_10m_populated_places.zip)

## `Downloads - 1:110m Cultural Vectors - Admin 0 – Countries`
- `ne_110m_admin_0_countries` 以国家为单位的地图边界数据，默认根据谁控制领土显示事实上的边界，而不是法律上的边界

> [Download countries (210.08 KB) version 5.1.1](https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip)

目前`Lets-Plot`在一些细节上不兼容`ne_110m_admin_0_countries`中`Africa`的数据，所以用时需要排除`CQL.toFilter("CONTINENT NOT LIKE 'Africa'")`

----------
> `aliyun-datav-`相关目录数据不是来自`Natural Earth`
# 自制中国数据
> `NaturalEarth/scripts/aliyun_datav_100000_generate_cn.py`
## 从[阿里云DataV数据可视化](https://datav.aliyun.com/portal/school/atlas/area_selector)导出`其他类型`会得到`中华人民共和国.json`
> 对的json数据中应包含属性`{"type":"FeatureCollection"}`

## 使用python对数据进行处理
```shell
pip install geopandas
```
### 1.读取json数据生成初始的Shapefile
```
import geopandas
from shapely.geometry import GeometryCollection

geoDataFrameFromJson = geopandas.read_file("中华人民共和国.json")
geoDataFrameFromJson.to_file(driver="ESRI Shapefile", filename="cache", encoding="utf-8")
```

### 2.读取初始的Shapefile进行处理
生成的初始Shapefile数据中存在一些拓扑结构冲突的问题，需要处理
```
initGeoDataFrame = geopandas.read_file("cache")
initGeoDataFrame.geometry = initGeoDataFrame.geometry.make_valid()
```
`make_valid()`又会导致新的`geometry`数据为`GeometryCollection`类型的[POLYGON, MULTILINESTRING]，然后保存到新的Shapefile中时不支持`GeometryCollection`且不支持包含`MULTILINESTRING`类型的数据，因此解开`GeometryCollection`，并只保留第一项`POLYGON`，`这一步操作会导致数据出现很多错误，需要优化`
```
def explode_geometrycollection(geom):
    if isinstance(geom, GeometryCollection):
        return [g for g in geom.geoms][0]
    else:
        return geom


initGeoDataFrame.geometry = initGeoDataFrame.geometry.apply(explode_geometrycollection)
```
重新生成正确的文件
```
initGeoDataFrame.to_file(driver="ESRI Shapefile", filename="../aliyun_datav_100000_cn", encoding="utf-8")
```