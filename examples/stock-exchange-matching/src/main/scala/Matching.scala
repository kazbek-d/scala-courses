import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import java.util.concurrent.ConcurrentHashMap


import model._

import scala.util.{Failure, Success}

trait Matching {

  import common.helper._
  implicit val timeout = Timeout(5 seconds)

  private val clients = new ConcurrentHashMap[String, Client]
  def getClients =  clients.values().toArray(Array[Client]())



  def addOrder(order: Order) = (ticker(order.asset.name) ? order) onComplete {
    case Success(value) => value match {
      //      case err: m.AnyErr => complete(StatusCodes.BadRequest, err)
      //      case err: m.InsufficientFunds => complete(StatusCodes.BadRequest, err)
      //
      //      case balance: m.Balance => complete(StatusCodes.Accepted, balance)
      case _ =>
    }
    case Failure(ex) =>
  }


}
