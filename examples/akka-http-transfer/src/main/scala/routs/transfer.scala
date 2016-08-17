package routs

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import akka.pattern.ask
import akka.util.Timeout
import model.{Transfer => m}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object transfer {

  import common.helper._
  import common.model_implicits._
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
