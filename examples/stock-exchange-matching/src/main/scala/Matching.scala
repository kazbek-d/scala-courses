package com

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import scala.util.{Failure, Success}


trait Matching extends Clients {

  import common.helper._
  import model._

  implicit val timeout = Timeout(5 seconds)

  def addOrder(order: Order) = (ticker(order.asset.name) ? order) onComplete {
    case Success(value) => value match {
      case xs:List[_] => updateClientsState {
        xs.collect { case m: MatchingOrders => m }
      }
      case _ =>
    }
    case Failure(ex) =>
  }


}
