import geopandas
from shapely.geometry import GeometryCollection

geoDataFrameFromJson = geopandas.read_file("中华人民共和国.json")
geoDataFrameFromJson.to_file(driver="ESRI Shapefile", filename="cache", encoding="utf-8")

initGeoDataFrame = geopandas.read_file("cache")
initGeoDataFrame.geometry = initGeoDataFrame.geometry.make_valid()


def explode_geometrycollection(geom):
    if isinstance(geom, GeometryCollection):
        return [g for g in geom.geoms][0]
    else:
        return geom


initGeoDataFrame.geometry = initGeoDataFrame.geometry.apply(explode_geometrycollection)
initGeoDataFrame.to_file(driver="ESRI Shapefile", filename="../aliyun_datav_100000_cn", encoding="utf-8")
