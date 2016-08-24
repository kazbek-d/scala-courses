package actors

import akka.actor.{Actor, ActorLogging}
import com.Clients
import model.{GetClients, ClientsList, SetMatchingOrders}

class UpdateClients extends Actor with ActorLogging with Clients {

  override def receive: Receive = {

    case SetMatchingOrders(xs) => {
      log.info(s"MatchingOrders comes: " + xs.mkString("\n"))
      updateClientsState(xs)
    }

    case ClientsList(xs) => {
      log.info(s"ClientsList comes: " + xs.mkString("\n"))
      setClients(xs)
    }

    case GetClients => {
      log.info(s"GetClients comes.")
      sender ! ClientsList(getClients)
    }

  }

}
