package common

import java.util.concurrent.ConcurrentHashMap

import actors.Account
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer


object implicits {
  private val actorSystemName: String = "my-system"

  implicit val system = ActorSystem(actorSystemName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}


object helper {
  import common.implicits._

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