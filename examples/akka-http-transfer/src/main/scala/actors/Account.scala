package actors

import model.Transfer._

import akka.actor.{Actor, ActorLogging}

class Account extends Actor with ActorLogging {

  var balance : BigDecimal = 0

  def receive: Receive = {

    case GetBalance(acc) => {
      log.info(s"AccGetBalance comes.")
      sender ! Balance(acc, balance)
    }

    case Deposit(acc, amount) => {
      log.info(s"Deposit ($amount)")
      balance += amount
      sender ! Balance(acc, balance)
    }

    case Withdraw(acc, amount) => {
      log.info(s"Withdraw ($amount)")
      sender ! {
        if(balance < amount)
          sender ! InsufficientFunds(s"InsufficientFunds ($balance < $amount)")
        else {
          balance -= amount
          sender ! Balance(acc, balance)
        }
      }
    }

  }
}