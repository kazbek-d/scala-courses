package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Visualization.{interpolateColor, predictTemperature}


object ImageHelper {

  def getImage(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)],
               w: Int = 360, h: Int = 180,
               zoom: Int = 0, x: Int = 0, y: Int = 0): Image = {

    val array =
      (0 until h).flatMap(y => {
        (0 until w).map(x => {
          val location = Tile(x, y, zoom.toShort, w, h).toLocation
          val predictedTemp = predictTemperature(temperatures, location)
          val color = interpolateColor(colors, predictedTemp)
          Pixel(color.red, color.green, color.blue, 127)
        })
      }).toArray

    Image(w, h, array)
  }


}
