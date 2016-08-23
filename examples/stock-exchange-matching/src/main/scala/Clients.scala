package com

import java.util.concurrent.ConcurrentHashMap

import model.{Client, MatchingOrders}

trait Clients {

  private val clients = new ConcurrentHashMap[String, Client]

  def setClients(xs: List[Client]) =
    xs.foreach(client => clients.put(client.name, client))

  def getClients = clients.values().toArray(Array[Client]()).toList

  def updateClientsState(xs: List[MatchingOrders]) = {
    xs.foreach(m => {

    })
  }

}
