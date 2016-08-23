package com

import java.util.concurrent.ConcurrentHashMap

import model.{Buy, Client, MatchingOrders, Order}

trait Clients {

  private val clients = new ConcurrentHashMap[String, Client]

  def setClients(xs: List[Client]) =
    xs.foreach(client => clients.put(client.name, client))

  def getClients = clients.values().toArray(Array[Client]()).toList

  def updateClient(order: Order): Unit = {
    val client = clients.get(order.clientName)
    if (client != null) {
      val sign = if (order.operation.equals(Buy)) 1 else -1
      val cash = client.cash + order.orderPrice * sign * -1
      val a = if (order.asset.name.equals("A")) client.a + order.quantity * sign else client.a
      val b = if (order.asset.name.equals("B")) client.b + order.quantity * sign else client.b
      val c = if (order.asset.name.equals("C")) client.c + order.quantity * sign else client.c
      val d = if (order.asset.name.equals("D")) client.b + order.quantity * sign else client.d
      clients.replace(order.clientName, client, client.copy(cash = cash, a = a, b = b, c = c, d = d))
    }
  }

  def updateClientsState(xs: List[MatchingOrders]) = {
    xs.foreach(m => {
      updateClient(m.buy)
      updateClient(m.sale)
    })
  }

}
