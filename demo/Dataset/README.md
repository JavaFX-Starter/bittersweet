# [Natural Earth](https://www.naturalearthdata.com/)
Natural Earth 是一个提供 1:10、1:50 和 1:110 百万比例尺公共领域地图数据集

## `Downloads - 1:10m Cultural Vectors - Populated Places`
- `ne_10m_populated_places` 城市和城镇点

> [Download populated places (2.68 MB) version 5.1.2](https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/ne_10m_populated_places.zip)

## `Downloads - 1:110m Cultural Vectors - Admin 0 – Countries`
- `ne_110m_admin_0_countries` 以国家为单位的地图边界数据，默认根据谁控制领土显示事实上的边界，而不是法律上的边界

> [Download countries (210.08 KB) version 5.1.1](https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/110m/cultural/ne_110m_admin_0_countries.zip)

----------
# [ChinaAdminDivisonSHP](https://github.com/GaryBikini/ChinaAdminDivisonSHP)
> 中国行政区划矢量图，ESRI Shapefile格式，共四级：国家、省/直辖市、市、区/县。关键字：中国行政区划图；中国地图；中国行政区；中国行政区地图；行政区地图；行政区；行政区划；地图；矢量数据；矢量地理数据；省级；直辖市；市级；区/县级；行政区划图。

这是GitHub上制作好的中国地图数据集，能够直接使用
----------
# [阿里云DataV数据可视化](https://datav.aliyun.com/portal/school/atlas/area_selector)
在DataV数据可视化平台选择`行政区划范围`为`中华人民共和国`，导出选择`其他类型(显示的部分数据包含"type": "FeatureCollection")`会得到`中华人民共和国.json`

## 直接加载json（处理起来比较简单）
使用`FeatureJSON`读取时根据报错调整json文件中最后一个对象`南海九段线`的`properties`属性内容与其他`格式`一致，然后在调用`toSpatialDataset`时根据报错移除用不到的数量不与其他属性一致的属性`centroid`，
`scripts目录下`，`中华人民共和国_原始数据.json`为初始数据，`中华人民共和国.json`为修改后的数据，总之要保证`features`的值的每一项的`properties`的内容格式一致

## 利用`python`的`geopandas`库读取`中华人民共和国_原始数据.json`文件并输出Shapefile文件
> `Dataset/AliYunDataV/scripts/aliyun_datav_100000_generate_cn.py`
处理方式较为粗糙，丢失了许多数据，待修
