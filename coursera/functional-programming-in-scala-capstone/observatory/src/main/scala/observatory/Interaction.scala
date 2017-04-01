package observatory

import com.sksamuel.scrimage.Image


/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x    X coordinate
    * @param y    Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location =
    Tile(x, y, zoom.toShort).toLocation

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @param zoom         Zoom level
    * @param x            X coordinate
    * @param y            Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {

    println(">>> tile:")
    println(">>> 1. temperatures: ")
    temperatures.foreach(println)
    println(">>> 2. colors: ")
    colors.foreach(println)
    println(">>> 3. zoom: " + zoom)
    println(">>> 4. x: " + x)
    println(">>> 5. y: " + y)

    ImageHelper.getImage(temperatures, colors,
      256, 256,
      zoom, x, y)

  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    *
    * @param yearlyData    Sequence of (year, data), where `data` is some data associated with
    *                      `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
                           yearlyData: Iterable[(Int, Data)],
                           generateImage: (Int, Int, Int, Int, Data) => Unit
                         ): Unit = {
    ???
//    yearlyData.foreach(year => {
//      (0 to 3).foreach(zoom => {
//        val size = math.pow(zoom + 1, 2).toInt
//        var xIndex = 0
//        var yIndex = 0
//        while (yIndex < size) {
//          xIndex = 0
//          while (xIndex < size) {
//            generateImage(year._1, zoom, xIndex, yIndex, year._2)
//            xIndex += 1
//          }
//          yIndex += 1
//        }
//      }
//      )
//    })

  }

}
