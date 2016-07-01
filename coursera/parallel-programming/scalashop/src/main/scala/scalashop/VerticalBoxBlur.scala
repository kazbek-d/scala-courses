package scalashop

import com.sun.javafx.tk.Toolkit.Task
import org.scalameter._
import common._

import scala.collection.immutable.IndexedSeq

object VerticalBoxBlurRunner {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 5,
    Key.exec.maxWarmupRuns -> 10,
    Key.exec.benchRuns -> 10,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val radius = 3
    val width = 1920
    val height = 1080
    val src = new Img(width, height)
    val dst = new Img(width, height)
    val seqtime = standardConfig measure {
      VerticalBoxBlur.blur(src, dst, 0, width, radius)
    }
    println(s"sequential blur time: $seqtime ms")

    val numTasks = 32
    val partime = standardConfig measure {
      VerticalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }

}

/** A simple, trivially parallelizable computation. */
object VerticalBoxBlur {

  /** Blurs the columns of the source image `src` into the destination image
   *  `dst`, starting with `from` and ending with `end` (non-inclusive).
   *
   *  Within each column, `blur` traverses the pixels by going from top to
   *  bottom.
   */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
    for (x <- math.max(from, 0) until math.min(end, src.width)) {
      for (y <- 0 until src.height) {
        dst.update(x, y, boxBlurKernel(src, x, y, radius))
      }
    }

    //    val xMin = math.max(from, 0)
    //    val xMax = math.min(end, src.width)
    //    val yMin = 0
    //    val yMax = src.height
    //
    //    var x = xMin
    //    var y = yMin
    //
    //    while (x < xMax) {
    //      while (y < yMax) {
    //        dst.update(x, y, boxBlurKernel(src, x, y, radius))
    //        y = y + 1
    //      }
    //      y = yMin
    //      x = x + 1
    //    }
  }

  /** Blurs the columns of the source image in parallel using `numTasks` tasks.
   *
   *  Parallelization is done by stripping the source image `src` into
   *  `numTasks` separate strips, where each strip is composed of some number of
   *  columns.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
    val numCols = src.width // Items count
    val step = math.max(1, numCols / numTasks) // max items in one task
    val starts = ( 0 until numCols by step ) :+ numCols // Make Array (0 to numCols). Array.Length = numTasks + 1 (numTasks < numCols, else numCols)
    val chunks = starts.zip(starts.tail) // Arr(0, 10, 20, 30, ...) zip (head and tail) for getting (from,end) like ( (0,10), (10, 20), (20, 30), ... )
    val tasks = for( (from,end) <- chunks ) yield task {
      blur(src, dst, from, end, radius)
    }
    tasks.foreach(_.join) // tasks.length must be numTasks or numCols (in case of numCols < numTasks)
  }

}
