package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import SparkImplicits._

import scala.annotation.tailrec



/**
  * 2nd milestone: basic visualization
  */
object Visualization { //extends App {

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

    var total = 0.0
    var sum = 0.0
    var distance = 0.0
    var weight = 0.0
    var value = 0.0
    val allPoints = temperatures.toList

    println(">>> predictTemperature:")
    println(">>> 1.temperatures:")
    temperatures.foreach(println)
    println(">>> 2.location:")
    println(location)

    var i = 0
    while (i < temperatures.size) {
      //distance = math.sqrt(math.pow(location.lat - allPoints(i)._1.lat, 2) + math.pow(location.lon - allPoints(i)._1.lon, 2))
      distance = greatCircleDistance(location, allPoints(i)._1)
      weight = 1 / distance // the weighting function
      value = weight * allPoints(i)._2 // the adjusted value
      total += value // sum it all up
      sum += weight
      i += 1
    }

    total / (if(sum == 0) 1 else sum)
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {

    println(">>> interpolateColor:")
    println(">>> 1.points:")
    points.foreach(println)
    println(">>> 2.value:")
    println(value)


    @tailrec
    def loop(xs: Iterable[(Double, Color)], acc: (Double, Int, Double, Int, Color, Color), counter: Int):
    (Option[Color], (Double, Int, Double, Int, Color, Color)) = xs match {
      case Nil => (None, acc)
      case (t, c) :: tail => if (t == value) (Some(c), acc) else {

        val pr = t < value && (t > acc._1 || acc._1 == value)
        val nx = t > value && (t < acc._3 || acc._3 == value)

        (
          if (pr) (t, c) else acc._1,
          if (pr) counter else acc._2,
          if (nx) (t, c) else acc._3,
          if (nx) counter else acc._4
        )

        loop(
          tail,
          (
            if (pr) t else acc._1,
            if (pr) counter else acc._2,
            if (nx) t else acc._3,
            if (nx) counter else acc._4,
            if (pr) c else acc._5,
            if (nx) c else acc._6
          ),
          counter + 1
        )

      }
    }

    val indexes = loop(points, (value, 0, value, 0, points.head._2, points.head._2), 0)

    def tempToColor(temp: Double): Color =
      if (temp >= 60) Color(255, 255, 255)
      else if (temp < 60 && temp >= 32) Color(255, 0, 0)
      else if (temp < 32 && temp >= 12) Color(255, 255, 0)
      else if (temp < 12 && temp >= 0) Color(0, 255, 255)
      else if (temp < 0 && temp >= -15) Color(0, 0, 255)
      else if (temp < -15 && temp >= -27) Color(255, 0, 255)
      else if (temp < -27 && temp >= -50) Color(33, 0, 107)
      else Color(0, 0, 0)

    val colsMap = List(60,32,12,0,-15,-27,-50,-60).map(t=> tempToColor(t) -> t).toMap

    if (indexes._1.isDefined) {
      indexes._1.get
    }
    else {
      val y_prev = colsMap.getOrElse(indexes._2._5, 0)
      val y_next = colsMap.getOrElse(indexes._2._6, 0)

      val slope = if (indexes._2._1 != indexes._2._3) 0.0 else (y_next - y_prev) / (indexes._2._3 - indexes._2._1)
      val y_point = y_prev + slope * (value - indexes._2._1)

      tempToColor(y_point)
    }
  }

//  val aaa = interpolateColor(List((0.0,Color(255,0,0)), (19.51699163993719,Color(0,0,255))), value = 29.51699163993719)
//  println(aaa)
  
  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {

    println(">>> visualize:")
    println(">>> 1.temperatures:")
    temperatures.foreach(println)
    println(">>> 2.colors:")
    colors.foreach(println)

    ???
  }

}


