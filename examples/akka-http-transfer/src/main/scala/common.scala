package common

import java.util.concurrent.ConcurrentHashMap

import actors.Account
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol._


object implicits {
  private val actorSystemName: String = "my-system"

  implicit val system = ActorSystem(actorSystemName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}


object model_implicits {
  import model.{Transfer => m}
  implicit val transfer2AccFormat = jsonFormat3(m.Transfer2Acc)
  implicit val balanceFormat = jsonFormat2(m.Balance)
  implicit val depositFormat = jsonFormat2(m.Deposit)
  implicit val withdrawFormat = jsonFormat2(m.Withdraw)
  implicit val getBalanceFormat = jsonFormat1(m.GetBalance)
  implicit val anyErrFormat = jsonFormat1(m.AnyErr)
  implicit val insufficientFunds = jsonFormat1(m.InsufficientFunds)}



object helper {
  import common.implicits._

  // TODO: use actorSelection
  private val accMap = new ConcurrentHashMap[String, ActorRef]
  def account(acc: String) : ActorRef = {
    val child = accMap.get(acc)
    if (child == null) {
      val actor: ActorRef = system.actorOf(Props[Account], name = acc)
      accMap.put(acc, actor)
      actor
    }
    else child
  }

}