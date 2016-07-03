package QueuePackage

case class CustomersPizza(id: Int, ti: Int, li: Int) {
  override def toString = s"Customer's Id: $id, waiting time: $ti, pizza preparing time: $li "
}

case class Result(latency: List[Int], customersOrder: List[Int], log: List[String]) {
  def minLatency = latency.sum / latency.length
  override def toString = s"Minimum Average Waiting Time = $minLatency, cooking order is: ${customersOrder.mkString(",")}"
}

trait CookTaskOptimizer {

  private class CookTask(
                          override val id: Int,
                          override val ti: Int,
                          override val li: Int,
                          var isCooking: Boolean,
                          var waitTime: Int,
                          var remindTime: Int
                        ) extends CustomersPizza(id, ti, li) {
    def move: CookTask = {
      waitTime = waitTime + 1
      if (isCooking) remindTime = remindTime - 1
      this
    }

    def startCooking: CookTask = {
      isCooking = true
      this
    }

    def waitFactor: Int = waitTime + remindTime

    override def toString: String =
      s"(Id:$id, ti:$ti, li:$li, isCooking:$isCooking, waitTime:$waitTime, remindTime:$remindTime)"
  }

  def calculate(list: List[CustomersPizza]): Result = {
    val gr = list.map(x => new CookTask(x.id, x.ti, x.li, false, 0, x.li)).groupBy(_.ti)

    @annotation.tailrec
    def loop(result: Result, acc: List[CookTask], step: Int): Result = {
      (for (t <- acc) yield {
        t.move
      }) ::: gr.getOrElse(step, Nil) match {
        case accM@_ :: _ =>
          val isPizzaReady = accM.exists(x => x.remindTime == 0 && x.isCooking)
          val isEmptyPan = accM.forall(x => !x.isCooking && x.remindTime > 0)
          if (isPizzaReady || isEmptyPan) {
            // Pizza ready (or empty Pan)! Who is next?
            accM.filter(!_.isCooking).sortBy(_.waitFactor) match {
              case head :: tail => {
                val accR = head.startCooking :: tail
                loop(
                  result.copy(
                    latency = result.latency :+ head.waitFactor,
                    log = result.log :+ accR.mkString(" + "),
                    customersOrder = result.customersOrder :+ head.id
                  ),
                  accR, step + 1)
              }
              case _ => result
            }
          } else {
            loop(result.copy(log = result.log :+ accM.mkString(" + ")), accM, step + 1)
          }
        case _ => result
      }
    }

    loop(Result(Nil, Nil, Nil), Nil, list.map(_.ti).min)
  }

}