package actors

import akka.actor.{Actor, ActorLogging}
import com.OrdersBook
import common.helper._
import model._

class Ticker extends Actor with ActorLogging with OrdersBook {

  override def receive: Receive = {

    case order: Order => {
      log.info(s"Order comes: " + order.toString)
      updateClients ! SetMatchingOrders(insertOrder(order))
    }

  }

}