package ch_akka_advance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated}
import akka.pattern.{AskTimeoutException, ask, pipe}
import akka.util.Timeout
import akka.actor.SupervisorStrategy.{Decider, Escalate, Restart, Resume, Stop}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random


object advance extends App {

  val system = ActorSystem("Coffeehouse")

  // Customer
  object Customer {

    case object CaffeineWithdrawalWarning

  }

  class Customer(coffeeSource: ActorRef) extends Actor with ActorLogging {

    import Customer._
    import Barista._
    import EspressoCup._
    import context.dispatcher

    context.watch(coffeeSource)

    def receive = {
      case CaffeineWithdrawalWarning => coffeeSource ! EspressoRequest
      case (EspressoCup(Filled), Receipt(amount)) =>
        log.info(s"yay, caffeine for ${self}!")
      case ComebackLater =>
        log.info("grumble, grumble")
        context.system.scheduler.scheduleOnce(300.millis) {
          coffeeSource ! EspressoRequest
        }
      case Terminated(barista) =>
        log.info("Oh well, let's find another coffeehouse...")
    }
  }


  // Barista
  object Barista {

    case object EspressoRequest

    case object ClosingTime

    case object ComebackLater

    case class EspressoCup(state: EspressoCup.State)

    object EspressoCup {

      sealed trait State

      case object Clean extends State

      case object Filled extends State

      case object Dirty extends State

    }

    case class Receipt(amount: Int)

  }

  class Barista extends Actor {

    import Barista._
    import Register._
    import EspressoCup._
    import context.dispatcher

    implicit val timeout = Timeout(1.seconds)

    val decider: Decider = {
      case _: PaperJamException => Resume
    }
    override val supervisorStrategy = OneForOneStrategy(10, 2.minutes)(decider.orElse(SupervisorStrategy.defaultStrategy.decider))


    val register = context.actorOf(Props[Register], "Register")

    def receive = {
      case EspressoRequest =>
        val receipt = register ? Transaction(Espresso)
        receipt.map((EspressoCup(Filled), _)).pipeTo(sender)
      case ClosingTime => context.stop(self)
    }
  }

  // Register
  object Register {

    sealed trait Article

    case object Espresso extends Article

    case object Cappuccino extends Article

    case class Transaction(article: Article)

    class PaperJamException(msg: String) extends Exception(msg)

  }

  class Register extends Actor with ActorLogging {

    import Register._
    import Barista._

    var revenue = 0
    val prices = Map[Article, Int](Espresso -> 150, Cappuccino -> 250)
    var paperJam = false

    override def postRestart(reason: Throwable): Unit = {
      super.postRestart(reason)
      log.info(s"Restarted because of ${reason.getMessage}")
      log.info(s"Restarted, and revenue is $revenue cents")
    }

    def receive = {
      case Transaction(article) =>
        val price = prices(article)
        sender ! createReceipt(price)
        revenue += price
        log.info(s"Revenue incremented to $revenue cents")
    }

    def createReceipt(price: Int): Receipt = {
      import util.Random
//      if (Random.nextBoolean()) paperJam = true
//      if (paperJam) throw new PaperJamException("OMG, not again!")
      Receipt(price)
    }
  }


  // ReceiptPrinter
  object ReceiptPrinter {

    case class PrintJob(amount: Int)

    class PaperJamException(msg: String) extends Exception(msg)

  }

  class ReceiptPrinter extends Actor with ActorLogging {

    import ReceiptPrinter._
    import Barista._

    var paperJam = false

    override def postRestart(reason: Throwable) {
      super.postRestart(reason)
      log.info(s"Restarted, paper jam == $paperJam")
    }

    def receive = {
      case PrintJob(amount) => sender ! createReceipt(amount)
    }

    def createReceipt(price: Int): Receipt = {
      if (Random.nextBoolean()) paperJam = true
      if (paperJam) throw new PaperJamException("OMG, not again!")
      Receipt(price)
    }
  }

  // RegisterNew
  class RegisterNew extends Actor with ActorLogging {

    implicit val timeout = Timeout(1.seconds)

    import Register._
    import Barista._
    import ReceiptPrinter._
    import context.dispatcher

    var revenue = 0
    val prices = Map[Article, Int](Espresso -> 150, Cappuccino -> 250)
    val printer = context.actorOf(Props[ReceiptPrinter], "Printer")

    override def postRestart(reason: Throwable) {
      super.postRestart(reason)
      log.info(s"Restarted, and revenue is $revenue cents")
    }

    def receive = {
      case Transaction(article) =>
        val price = prices(article)
        val requester = sender
        (printer ? PrintJob(price)).map((requester, _)).pipeTo(self)
      case (requester: ActorRef, receipt: Receipt) =>
        revenue += receipt.amount
        log.info(s"revenue is $revenue cents")
        requester ! receipt
    }
  }


  // BaristaNew
  class BaristaNew extends Actor {

    import Barista._
    import Register._
    import EspressoCup._
    import context.dispatcher

    implicit val timeout = Timeout(1.seconds)

    val register = context.actorOf(Props[RegisterNew], "RegisterNew")

    def receive = {
      case EspressoRequest =>
        val receipt = register ? Transaction(Espresso)
        receipt.map((EspressoCup(Filled), _)).recover {
          case _: AskTimeoutException => ComebackLater
        } pipeTo sender
      case ClosingTime => context.stop(self)
    }
  }


  import Customer._
  implicit val timeout = Timeout(1.second)

  // simple send command
  val barista = system.actorOf(Props[Barista], "Barista")
  val customerJohnny = system.actorOf(Props(classOf[Customer], barista), "Johnny")
  val customerAlina = system.actorOf(Props(classOf[Customer], barista), "Alina")
  customerJohnny ! CaffeineWithdrawalWarning
  customerAlina ! CaffeineWithdrawalWarning


  // better solution
  val baristanew = system.actorOf(Props[BaristaNew], "Baristanew")
  val customerRob = system.actorOf(Props(classOf[Customer], baristanew), "Rob")
  val customerAndy = system.actorOf(Props(classOf[Customer], baristanew), "Andy")
  customerRob ! CaffeineWithdrawalWarning
  customerAndy ! CaffeineWithdrawalWarning


  StdIn.readLine()
  baristanew ! PoisonPill
  StdIn.readLine()

  system.terminate()

}
