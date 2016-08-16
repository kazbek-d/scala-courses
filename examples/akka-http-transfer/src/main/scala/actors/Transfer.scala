package actors

import model.Transfer._

import akka.actor.{Actor, ActorLogging}

class Transfer extends Actor with ActorLogging {

  def receive: Receive = {

    case GetBalance(acc) =>{
      log.info(s"GetBalance ($acc)")
      sender ! Balance(acc, 1000)
    }

    case Deposit(acc, amount) => {
      log.info(s"Deposit ($acc, $amount)")
      sender ! OK
    }

    case Withdraw(acc, amount) => {
      log.info(s"Withdraw ($acc, $amount)")
      sender ! OK
    }

    case Transfer2Acc(acc1, acc2, amount) => {
      log.info(s"Transfer2Acc ($acc1, $acc2, $amount)")
      sender ! OK
    }

  }
}
