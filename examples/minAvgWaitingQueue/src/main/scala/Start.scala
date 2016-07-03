

import QueuePackage.{CookTaskOptimizer, CustomersPizza}

import scala.io.StdIn
import scala.util.Try

object Start extends App with CookTaskOptimizer with Utils {

  println("Enter customers count: ")
  val customersOrderList: List[CustomersPizza] =
    (for (index <- 1 to loadCount)
      yield { loadCustomersOrder(index) } ).toList

  println()
  println("Calculating minimum average waiting time for:")
  customersOrderList.foreach(println)

  val result = process(customersOrderList)
  println()
  println("Solution:")
  result.solution.foreach(println)
  println()
  println("Result:")
  println(result)

}

trait Utils {
  def loadLine = StdIn.readLine.split(" ") map (x => Try(x.toInt).toOption) filter (_.isDefined) map (_.get)

  def loadCount : Int = {
    val count = loadLine
    if (count.length == 0) 0 else count(0)
  }

  def loadCustomersOrder(index: Int): CustomersPizza = {
    println(s"Enter task $index. ( Format is 'CustomerNumber WaitingTime'. Example: '1 6' )")
    try {
      val line = loadLine
      CustomersPizza(index, line(0), line(1))
    } catch {
      case e: Exception => {
        println("Input string wrong format. Try again")
        loadCustomersOrder(index)
      }
    }
  }

}