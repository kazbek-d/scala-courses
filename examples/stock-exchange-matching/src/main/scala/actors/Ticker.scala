package actors

import akka.actor.{Actor, ActorLogging}
import com.OrdersBook
import model.Order

class Ticker extends Actor with ActorLogging with OrdersBook {

  override def receive: Receive = {

    case order: Order => {
      log.info(s"Order comes." + order.toString)
      sender ! insertOrder(order)
    }

  }

}
