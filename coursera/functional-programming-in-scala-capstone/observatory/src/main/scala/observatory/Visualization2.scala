package observatory

import com.sksamuel.scrimage.Image

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  /**
    * @param x X coordinate between 0 and 1
    * @param y Y coordinate between 0 and 1
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double
  ): Double = {
    BilinearInterpolation(d01, d00, d11, d10, -180, 0, 0, 90, x, y)
  }

  def BilinearInterpolation(q11: Double, q12: Double, q21: Double, q22: Double,
                            x1: Double, x2: Double, y1: Double, y2: Double,
                            x: Double, y: Double) : Double =
  {
    val x2x1 = x2 - x1
    val y2y1 = y2 - y1
    val x2x = x2 - x
    val y2y = y2 - y
    val yy1 = y - y1
    val xx1 = x - x1

    1.0 / (x2x1 * y2y1) * (
      q11 * x2x * y2y +
        q21 * xx1 * y2y +
        q12 * x2x * yy1 +
        q22 * xx1 * yy1
      )
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param zoom Zoom level of the tile to visualize
    * @param x X value of the tile to visualize
    * @param y Y value of the tile to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int
  ): Image = {
    // Interaction.tile(_, colors, zoom, x,y )
    ???
  }

}
