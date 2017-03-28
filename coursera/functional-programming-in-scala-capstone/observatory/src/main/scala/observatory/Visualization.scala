package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import SparkImplicits._

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
    val (totalTemp, count) =
      spark.sparkContext.parallelize(temperatures.toList).map({
        case (point, temperatura) =>
          val distance = greatCircleDistance(location, point)
          val weight = 1 / distance // the weighting function
        val value = weight * temperatura // the adjusted value
          (value, 1)
      }).reduce((a, b) => (a._1 + b._1, a._2 + b._2))
    totalTemp / count
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    ???
  }

}

