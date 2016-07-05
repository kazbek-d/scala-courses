package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {

    //    @annotation.tailrec
    //    def loop(idx: Int, until: Int, index: Int, acc: Int): Int = {
    //      if (idx + index >= until)
    //        acc
    //      else {
    //        loop(idx, until,
    //          index + 1,
    //          if (chars(idx + index) == '(') acc + 1
    //          else if (chars(idx + index) == ')') acc - 1
    //          else acc
    //        )
    //      }
    //    }
    //loop(0, chars.length, 0, 0) == 0

    var index = 0
    var acc = 0
    while (index < chars.length) {
      if (chars(index) == '(') acc += 1
      else if (chars(index) == ')') acc -= 1

      if (acc < 0) index = chars.length
      else index += 1
    }
    acc == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    @annotation.tailrec
    def traverse(idx: Int, until: Int, acc: Int, index: Int) : BigInt = {
      if (idx + index >= until) acc
      else {
        val ac: Int = if (chars(idx + index) == '(') acc + 1 else if (chars(idx + index) == ')') acc - 1 else acc
        if(idx == 0 && ac < 0) Int.MaxValue else traverse(idx, until, ac, index + 1)
      }
    }

    def reduce(from: Int, until: Int) : BigInt = {
      if(until - from == 0) 0
      else if (until - from < threshold || threshold < 2)
        traverse(from, until, 0, 0)
      else {
        val mid = (from + until) / 2
        val (left, right) = parallel(reduce(from, mid), reduce(mid, from))

        if (left == Int.MaxValue || right == Int.MaxValue) Int.MaxValue
        else left + right
      }
    }

    reduce(0, chars.length) == 0
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
