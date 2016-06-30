
import common._

package object scalashop {

  /** The value of every pixel is represented as a 32 bit integer. */
  type RGBA = Int

  /** Returns the red component. */
  def red(c: RGBA): Int = (0xff000000 & c) >>> 24

  /** Returns the green component. */
  def green(c: RGBA): Int = (0x00ff0000 & c) >>> 16

  /** Returns the blue component. */
  def blue(c: RGBA): Int = (0x0000ff00 & c) >>> 8

  /** Returns the alpha component. */
  def alpha(c: RGBA): Int = (0x000000ff & c) >>> 0

  /** Used to create an RGBA value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA = {
    (r << 24) | (g << 16) | (b << 8) | (a << 0)
  }

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int = {
    if (v < min) min
    else if (v > max) max
    else v
  }

  /** Image is a two-dimensional matrix of pixel values. */
  class Img(val width: Int, val height: Int, private val data: Array[RGBA]) {
    def this(w: Int, h: Int) = this(w, h, new Array(w * h))
    def apply(x: Int, y: Int): RGBA = data(y * width + x)
    def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c
  }

  /** Computes the blurred RGBA value of a single pixel of the input image. */
  def boxBlurKernel(src: Img, x: Int, y: Int, radius: Int): RGBA = {

    val xMin = if (x - radius < 0) 0 else x - radius
    val xMax = if (x + radius > src.width) src.width else x + radius
    val yMin = if (y - radius < 0) 0 else y - radius
    val yMax = if (y + radius > src.height) src.height else y + radius

    var xIndex = xMin
    var yIndex = yMin

    var counter = 0
    var r: Int = 0
    var g: Int = 0
    var b: Int = 0
    var a: Int = 0

    while (yIndex <= yMax) {
      while (xIndex <= xMax) {
        if (xIndex != x || yIndex != y) {
          counter = counter + 1
          r = r + red(src(xIndex, yIndex))
          g = g + green(src(xIndex, yIndex))
          b = b + blue(src(xIndex, yIndex))
          a = a + alpha(src(xIndex, yIndex))
        }
        xIndex = xIndex + 1
      }
      xIndex = xMin
      yIndex = yIndex + 1
    }

    def f(x: Int): Int = clamp(x / counter, 0, 255)

    if(counter == 0) src(x, y)
    else rgba(f(r), f(g), f(b), f(a))
  }

}
