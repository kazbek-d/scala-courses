package routs

import actors.SalesDataActor
import akka.actor.Props
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import akka.pattern.ask
import akka.util.Timeout
import data.Repository
import model.Sales.SalesResponces
import model.{Sales => m}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class SalesRout(implicit repository: Repository) {

  import common.implicits._
  import common.model_implicits._

  implicit val timeout: Timeout = 5.seconds

  private val makeResult: (Try[Any]) => StandardRoute = {
    case Success(value) => value match {
      case err: m.AnyErr => complete(StatusCodes.BadRequest, err)
      case salesData : SalesResponces =>
        complete(StatusCodes.Accepted, salesData)
      case _ => complete(StatusCodes.Accepted, "Ok")
    }
    case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
  }

  val salesDataActor = system.actorOf(Props(new SalesDataActor), "salesDataActor")

  val salesRoute = pathPrefix("test") {

    path("get-sales-by-period") {
      post {
        entity(as[m.SalesByPeriod]) { request =>
          onComplete(salesDataActor ? request)(makeResult)
        }
      }
    } ~
      path("get-sales-by-shop") {
        post {
          entity(as[m.SalesByShop]) { request =>
            onComplete(salesDataActor ? request)(makeResult)
          }
        }
      } ~
      path("get-sales-by-shop-product") {
        post {
          entity(as[m.SalesByShopProduct]) { request =>
            onComplete(salesDataActor ? request)(makeResult)
          }
        }
      } ~
      path("get-sales-by-shop-price") {
        post {
          entity(as[m.SalesByShopPrice]) { request =>
            onComplete(salesDataActor ? request)(makeResult)
          }
        }
      }

  }

}
