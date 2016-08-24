package com

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._


trait Matching {

  import common.helper._
  import model._

  implicit val timeout = Timeout(5 seconds)

  def setClients(xs: List[Client]) =
    updateClients ! ClientsList(xs)

  def getClients =
    updateClients ? GetClients

  def addOrder(order: Order) =
    ticker(order.asset.name) ! order

}
