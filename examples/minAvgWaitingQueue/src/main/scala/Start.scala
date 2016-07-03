

import QueuePackage.CustomersPizza

import scala.io.StdIn
import scala.util.Try

object Start {
  def main(args: Array[String]) {

    def loadLine = StdIn.readLine.split(" ") map (x => Try(x.toInt).toOption) filter (_.isDefined) map (_.get)

    println("Enter customers count: ")
    val count = loadLine

    val customersOrderList: List[CustomersPizza] =
      (for (i <- 1 to ( if(count.length == 0) 0 else count(0)))
        yield {
          @annotation.tailrec
          def loadCustomersOrder(acc: Set[Int]): CustomersPizza = {
            println(s"Enter task $i. ( Format is 'CustomerNumber WaitingTime'. Example: '1 6' )")
            try {
              val line = loadLine
              CustomersPizza(i, line(0), line(1))
            } catch {
              case e: Exception => {
                println("Input string wrong format. Try again")
                loadCustomersOrder(acc)
              }
            }
          }
          loadCustomersOrder(Set.empty)
        }).toList

    println("Calculating minimum average latency for:")
    customersOrderList.map(x => s"Customer's Id: ${x.id}, waiting time: ${x.ti}, pizza preparing time: ${x.li} ").foreach(println)
    println("Result:")

  }
}
