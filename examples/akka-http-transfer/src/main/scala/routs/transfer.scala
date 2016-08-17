package routs

import java.util.concurrent.ConcurrentHashMap

import actors.Account
import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import akka.pattern.ask
import akka.util.Timeout
import model.{Transfer => m}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object transfer {

  implicit val transfer2AccFormat = jsonFormat3(m.Transfer2Acc)
  implicit val balanceFormat = jsonFormat2(m.Balance)
  implicit val depositFormat = jsonFormat2(m.Deposit)
  implicit val withdrawFormat = jsonFormat2(m.Withdraw)
  implicit val getBalanceFormat = jsonFormat1(m.GetBalance)
  implicit val anyErrFormat = jsonFormat1(m.AnyErr)
  implicit val insufficientFunds = jsonFormat1(m.InsufficientFunds)

  import common.helper._
  implicit val timeout: Timeout = 5.seconds

  private val makeResult: (Try[Any]) => StandardRoute = {
    case Success(value) => value match {
      case err: m.AnyErr => complete(StatusCodes.BadRequest, err)
      case err: m.InsufficientFunds => complete(StatusCodes.BadRequest, err)

      case balance: m.Balance => complete(StatusCodes.Accepted, balance)
      case _ => complete(StatusCodes.Accepted, "Ok")
    }
    case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
  }

  val transferRoute = pathPrefix("transfer") {

    path("GetBalance") {
      post {
        entity(as[m.GetBalance]) { getBalance =>
          val account1: Unit = account(getBalance.acc)
          onComplete(account(getBalance.acc) ? getBalance) (makeResult)
        }
      }
    } ~
      path("Deposit") {
        post {
          entity(as[m.Deposit]) { deposit =>
            onComplete(account(deposit.acc) ? deposit) (makeResult)
          }
        }
      } ~
      path("Withdraw") {
        post {
          entity(as[m.Withdraw]) { withdraw =>
            onComplete(account(withdraw.acc) ? withdraw) (makeResult)
          }
        }
      } ~
      path("Transfer2Acc") {
        post {
          entity(as[m.Transfer2Acc]) { transfer2Acc =>
            onComplete(account(transfer2Acc.acc1) ? transfer2Acc) (makeResult)
          }
        }
      }
  }

}
