package observatory

import scala.math._

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class Tile(x: Int, y: Int, z: Short = 1, w: Double = 360.0, h: Double = 180.0) {
  def toLatLon = LatLonPoint(
    toDegrees(atan(sinh(Pi * (1.0 - 2.0 * y.toDouble / (1 << z))))),
    x.toDouble / (1 << z) * w - h,
    z)

  def toLocation = {
    val latLonPoint = toLatLon
    Location(latLonPoint.lat, latLonPoint.lon)
  }
}

case class LatLonPoint(lat: Double, lon: Double, z: Short = 1, w: Double = 360.0, h: Double = 180.0) {
  def toTile = LatLonPoint(
    ((lon + h) / w * (1 << z)).toInt,
    ((1 - log(tan(toRadians(lat)) + 1 / cos(toRadians(lat))) / Pi) / 2.0 * (1 << z)).toInt,
    z)
}
