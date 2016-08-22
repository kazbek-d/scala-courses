package actors

import akka.actor.{Actor, ActorLogging}
import common.OrdersHeap
import model.{Buy, Order, Sale}

class Ticker extends Actor with ActorLogging with OrdersHeap {

  var buy: H = empty
  var sale: H = empty

  override def receive: Receive = {

    case order: Order => {

      order.operation match {
        case Buy => buy = insert(order, buy)
        case Sale =>sale = insert(order.copy(price = -order.price), sale)
      }

      log.info(s"Order comes." + order.toString)
      sender ! ""
    }

  }

}
