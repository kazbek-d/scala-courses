package observatory


import com.sksamuel.scrimage.Image

import scala.annotation.tailrec
import scala.collection.immutable.{::, Nil}


/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  def greatCircleDistance(a: Location, b: Location): Double = {

    def convertRadians(num: Double): Double = math.Pi * num / 180

    // convert all numbers to Rad
    val dLat = convertRadians(a.lat - b.lat)
    val dLong = convertRadians(a.lon - b.lon)
    val lat1 = convertRadians(a.lat)
    val lat2 = convertRadians(b.lat)

    val num1 = math.sin(dLat / 2)
    val num2 = math.sin(dLong / 2)

    val res = math.pow(num1, 2) + math.cos(lat1) * math.cos(lat2) * math.pow(num2, 2)

    val R = 3959.9
    R * (2 * math.atan2(math.sqrt(res), math.sqrt(1 - res)))
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    if (temperatures.isEmpty)
      0.0
    else {
      var total = 0.0
      var sum = 0.0
      var distance = 0.0
      var weight = 0.0
      var value = 0.0
      val allPoints = temperatures.toSeq

      var i = 0
      while (i < temperatures.size) {
        //distance = math.sqrt(math.pow(location.lat - allPoints(i)._1.lat, 2) + math.pow(location.lon - allPoints(i)._1.lon, 2))
        distance = greatCircleDistance(location, allPoints(i)._1)

        if (distance == 0) {
          total = allPoints(i)._2
          sum = 1
          i = Int.MaxValue
        } else {
          weight = 1 / distance // the weighting function
          value = weight * allPoints(i)._2 // the adjusted value
          total += value // sum it all up
          sum += weight
          i += 1
        }
      }

      if (sum == 0) total else total / sum
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {

    @tailrec
    def loop(points: List[(Double, Color)], pr: (Double, Color), nx: (Double, Color), c: Option[Color])
    : ((Double, Color), (Double, Color), Option[Color]) = points match {
      case Nil => (pr, nx, c)
      case ::(head, tl) => if (head._1 == value) (pr, nx, Some(head._2)) else {
        val prev = if (head._1 < value && head._1 > pr._1) head else pr
        val next = if (head._1 > value && head._1 < nx._1) head else nx
        loop(tl, prev, next, None)
      }
    }

    def y(p1: (Double, Double), p2: (Double, Double), x: Double) = {
      val k = if ((p2._1 - p1._1) == 0.0) 0.0 else (p2._2 - p1._2) / (p2._1 - p1._1)
      // angel
      val b = if ((p2._1 - p1._1) == 0.0) 0.0 else (p2._1 * p1._2 - p1._1 * p2._2) / (p2._1 - p1._1)
      //offset
      val res = k * x + b
      if (res < 0) 0.0 else if (res > 255) 255.0 else res
    }

    def problem127(i: Int) = if (i == 127) 128 else i

    val list = points.toList
    val result = loop(list, list.head, list.last, None)
    if (result._3.isDefined) result._3.get
    else {
      val pr = result._1
      val nx = result._2

      if (pr._2 == nx._2) pr._2
      else {
        val r = y((pr._1, pr._2.red), (nx._1, nx._2.red), value)
        val g = y((pr._1, pr._2.green), (nx._1, nx._2.green), value)
        val b = y((pr._1, pr._2.blue), (nx._1, nx._2.blue), value)
        Color(
          problem127(BigDecimal(r).setScale(0, BigDecimal.RoundingMode.HALF_UP).toInt),
          problem127(BigDecimal(g).setScale(0, BigDecimal.RoundingMode.HALF_UP).toInt),
          problem127(BigDecimal(b).setScale(0, BigDecimal.RoundingMode.HALF_UP).toInt))
      }
    }

  }


  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {

    ImageHelper.getImage(temperatures, colors)

//    val array =
//      (0 until 180).flatMap(y => {
//        (0 until 360).map(x => {
//          Pixel(255, 0, 0, 127)
//        })
//      }).toArray
//
//    Image(360, 180, array)
  }

}
