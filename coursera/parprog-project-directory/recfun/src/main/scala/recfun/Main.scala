package recfun

import scala.collection.immutable.::

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
    * Exercise 1
    */
  def pascal(c: Int, r: Int): Int =
    if (c > r)
      throw new java.lang.IllegalArgumentException
    else if (c == r || r == 0 || c == 0)
      1
    else
      pascal(c - 1, r - 1) + pascal(c, r - 1)


  /**
    * Exercise 2
    */
  def balance(chars: List[Char]): Boolean = if (chars.isEmpty)
    false
  else {

    def rec(chars: List[Char], acc: Int): Boolean = chars match {
      case head :: tail => head match {
        case '(' => rec(tail, acc + 1)
        case ')' => if (acc == 0) false else rec(tail, acc - 1)
        case _ => rec(tail, acc)
      }
      case _ => acc == 0
    }

    rec(chars, 0)
  }

  /**
    * Exercise 3
    */
  def countChange(money: Int, coins: List[Int]): Int = if (money == 0 || coins.isEmpty)
    0
  else {

    val xs = coins.filter(_ <= money).sorted
    def noPennies(coins: List[Int]): Boolean = coins match {
      case head :: tail => if (money % head == 0) false else noPennies(tail)
      case _ => true
    }
    if (noPennies(xs))
      0
    else {
      def rec(money: Int, coins: List[Int], coinIndex: Int): Int = {
        if (money < 0) 0
        else {
          if (money == 0) 1
          else {
            if (coinIndex == coins.length && money > 0) 0
            else
              rec(money - coins(coinIndex), coins, coinIndex) + rec(money, coins, coinIndex + 1)
          }
        }
      }
      rec(money, xs, 0)
    }
  }


}
