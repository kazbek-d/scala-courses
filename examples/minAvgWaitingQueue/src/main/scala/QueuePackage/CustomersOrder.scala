package QueuePackage

case class CustomersPizza(id: Int, ti: Int, li: Int) {
  override def toString = s"Customer's Id: $id, waiting time: $ti, pizza preparing time: $li "
}

case class Result(minLatency: Int, customersOrder: List[Int], solution: List[String]) {
  override def toString = s"Minimum Average Waiting Time = $minLatency, cooking order is: ${customersOrder.mkString(",")}"
}

trait CookTaskOptimizer {

  private class CookTask(
                          override val id: Int,
                          override val ti: Int,
                          override val li: Int,
                          waitTime: Int,
                          remindTime: Int
                        ) extends CustomersPizza(id, ti, li) {
    override def toString: String  = {
      " "
    }
  }

  def process(list: List[CustomersPizza]): Result = {
    Result(10, List(1, 2, 3), List("OK", "OK1", "Ok2"))
  }

}