package common

import java.util.concurrent.ConcurrentHashMap

import actors.{Ticker, UpdateClients}
import akka.actor.{ActorRef, ActorSystem, Props}


object helper {

  val system = ActorSystem("mySystem")

  val updateClients: ActorRef = system.actorOf(Props[UpdateClients], name = "UpdateClients")

  // TODO: use actorSelection
  private val accMap = new ConcurrentHashMap[String, ActorRef]
  def ticker(assetName: String) : ActorRef = {
    val child = accMap.get(assetName)
    if (child == null) {
      val actor: ActorRef = system.actorOf(Props[Ticker], name = assetName)
      accMap.put(assetName, actor)
      actor
    }
    else child
  }

}