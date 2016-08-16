package routs

import akka.actor.Props
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import akka.pattern.ask
import akka.util.Timeout
import model.{Transfer => m}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object transfer {

  implicit val transfer2AccFormat = jsonFormat3(m.Transfer2Acc)
  implicit val balanceFormat = jsonFormat2(m.Balance)
  implicit val depositFormat = jsonFormat2(m.Deposit)
  implicit val withdrawFormat = jsonFormat2(m.Withdraw)
  implicit val getBalanceFormat = jsonFormat1(m.GetBalance)

  import common.implicits._
  implicit val timeout: Timeout = 5.seconds

  private val transfer = system.actorOf(Props[actors.Transfer], "transfer")
  private val makeResult: (Try[Any]) => StandardRoute = {
    case Success(value) => value match {
      case m.AnyErr(err) => complete(StatusCodes.BadRequest, err)
      case _ => complete(StatusCodes.Accepted, "Ok")
    }
    case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
  }


  val transferRoute = pathPrefix("transfer") {

      path("GetBalance") {
        post {
          entity(as[m.GetBalance]) { getBalance =>
            val balance: Future[m.Balance] = (transfer ? getBalance).mapTo[m.Balance]
            complete(balance)
          }
        }
      } ~
      path("Deposit") {
        post {
          entity(as[m.Deposit]) { deposit =>
            onComplete(transfer ? deposit) (makeResult)
          }
        }
      } ~
        path("Withdraw") {
          post {
            entity(as[m.Withdraw]) { withdraw =>
              onComplete(transfer ? withdraw) (makeResult)
            }
          }
        } ~
        path("Transfer2Acc") {
          post {
            entity(as[m.Transfer2Acc]) { transfer2Acc =>
              onComplete(transfer ? transfer2Acc) (makeResult)
            }
          }
        }
  }

}
