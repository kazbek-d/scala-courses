package Ch13_Actors_and_Concurrency

import akka.pattern.gracefulStop
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props}
import scala.language.postfixOps
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._


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

class Person(name: String) extends Actor with ActorLogging {
  override def receive: Receive = {
    case "Hello" =>
      log.info(s"$name: Hello cames")
      println("Hi there")
    case _ =>
      log.info(s"$name: What?")
      log.info("something cames")
  }
}


case object SayHello
case object SayGoodBuy
case object ForceRestart
case object Stop
class PersonProcessor(personActorRef: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case SayHello =>
      log.info("SayHello cames")
      personActorRef ! "Hello"
    case ForceRestart =>
      log.info("ForceRestart cames")
      throw new Exception("Boom!")
    case _ =>
      log.info("_ cames")
      personActorRef ! "???"
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("preRestart")
    println(s" MESSAGE: ${message.getOrElse("")}")
    println(s" REASON: ${reason.getMessage}")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("postRestart")
    println(s" REASON: ${reason.getMessage}")
    super.postRestart(reason)
  }

  override def preStart(): Unit = {
    println("preStart")
    super.preStart()
  }

  override def postStop(): Unit = {
    println("postStop")
    super.postStop()
  }
}


class Parent extends Actor with ActorLogging {
  override def receive: Receive = {
    case Stop => context.stop(self)
    case _ => "Parent: Something was came"
  }
  override def preStart(): Unit = {
    context.actorOf(Props[Child], "childActorRef")
    super.preStart()
  }

  override def postStop(): Unit = {
    println("Parent postStop >>>>>>>>>>>")
    super.postStop()
  }
}
class Child extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => "Child: Something was came"
  }
  override def postStop(): Unit = {
    println("Child was stoped")
    super.postStop()
  }
}


object ConsoleApp extends App {

  val actorSystem = ActorSystem("hello_system")
  val helloActorRef = actorSystem.actorOf(Props[HelloActor], "helloActor")
  val personActorRef = actorSystem.actorOf(Props(new Person("Fred")), "personActor")
  val personProcessorRef = actorSystem.actorOf(Props(new PersonProcessor(personActorRef)), "personProcessorRef")
  val parentActorRef = actorSystem.actorOf(Props[Parent], "parentActorRef")

  //  helloActorRef ! "Hello"
  //  helloActorRef ! "Hell"
  //
  //  personActorRef ! "Hello"
  //  personActorRef ! "Hell"

  personProcessorRef ! SayHello
  Thread.sleep(500)
  personProcessorRef ! Kill
  Thread.sleep(1000)
  //  personProcessorRef ! SayGoodBuy
  //  Thread.sleep(500)
  //  personProcessorRef ! ForceRestart
  //  Thread.sleep(500)


  // lookup childActorRef, then kill it
  println("Sending childActorRef a PoisonPill ...")
  val child = actorSystem.actorSelection("/user/parentActorRef/childActorRef")
  child ! PoisonPill
  println("childActorRef was killed")
  Thread.sleep(500)

  parentActorRef ! Stop
  Thread.sleep(500)

  try {
    val stopped: Future[Boolean] = gracefulStop(helloActorRef, 2 seconds)
    Await.result(stopped, 3 seconds)
    println("testActor was stopped")
  } catch {
    case e: Exception => e.printStackTrace
  } finally {

  }
  Thread.sleep(2000)



  println("terminate")
  actorSystem.terminate

}

