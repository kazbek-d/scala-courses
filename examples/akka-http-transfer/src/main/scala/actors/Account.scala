package actors

import model.Transfer._

import akka.actor.{Actor, ActorLogging}

class Account extends Actor with ActorLogging {

  var balance : BigDecimal = 0

  def receive: Receive = {

    case AccGetBalance =>{
      log.info(s"AccGetBalance comes.")
      sender ! balance
    }

    case AccDeposit(amount) => {
      log.info(s"Deposit ($amount)")
      balance += amount
      sender ! OK
    }

    case AccWithdraw(amount) => {
      log.info(s"Withdraw ($amount)")
      sender ! {
        if(balance < amount) InsufficientFunds(s"($balance < $amount")
        else {
          balance -= amount
          OK
        }
      }
    }



  }
}