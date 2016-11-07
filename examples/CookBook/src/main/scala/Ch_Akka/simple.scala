package ch_akka_simple

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._


object simple extends App {

  val system = ActorSystem("Barista")

  // Customer
  case object CaffeineWithdrawalWarning
  class Customer(caffeineSource: ActorRef) extends Actor with ActorLogging {
    def receive = {
      case CaffeineWithdrawalWarning => caffeineSource ! EspressoRequest
      case Bill(cents) => println(s"I have to pay $cents cents, or else!")
    }
  }



  // Barista
  sealed trait CoffeeRequest
  case object CappuccinoRequest extends CoffeeRequest
  case object EspressoRequest extends CoffeeRequest
  case class Bill(cents: Int)
  case object ClosingTime
  class Barista extends Actor with ActorLogging {
    var cappuccinoCount = 0
    var espressoCount = 0
    def receive = {
      case CappuccinoRequest =>
        sender ! Bill(250)
        cappuccinoCount += 1
        println(s"I have to prepare cappuccino #$cappuccinoCount")
      case EspressoRequest =>
        sender ! Bill(200)
        espressoCount += 1
        println(s"Let's prepare espresso #$espressoCount.")
      case ClosingTime => context.system.terminate()
    }
  }



  // simple send command
  val barista = system.actorOf(Props[Barista], "Barista2")
  val customer = system.actorOf(Props(classOf[Customer], barista), "Customer")
  customer ! CaffeineWithdrawalWarning
  //barista ! ClosingTime

  // ask question
  implicit val timeout = Timeout(2.second)
  implicit val ec = system.dispatcher
  val barista2 = system.actorOf(Props[Barista], "Barista")
  val f: Future[Any] = barista2 ? CappuccinoRequest
  f.onSuccess {
    case Bill(cents) => println(s"Will pay $cents cents for a cappuccino")
  }
  (1 to 10).foreach(_ => barista2 ? CappuccinoRequest)

  system.terminate()
}
