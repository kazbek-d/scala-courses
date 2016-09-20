package Ch13_Actors_and_Concurrency

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}


class HelloActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case "Hello" =>
      log.info("Hello cames")
      println("Hi there")
    case _ =>
      println("What?")
      log.info("something cames")
  }
}

object L01 extends App {

  val system = ActorSystem("hello_system")
  val helloActorRef = system.actorOf(Props[HelloActor], "helloActor")
  helloActorRef ! "Hello"
  helloActorRef ! "Hell"
  system.terminate

}
