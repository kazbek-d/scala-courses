package common

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import spray.json.DefaultJsonProtocol._
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}


object implicits {
  private val actorSystemName: String = "sales-actor-system"

  implicit val system = ActorSystem(actorSystemName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}


object model_implicits {

  import model.{Sales => m}

  implicit object ZonedDateTimeProtocol extends RootJsonFormat[ZonedDateTime] with DefaultJsonProtocol {

    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault)

    def write(obj: ZonedDateTime): JsValue = {
      JsString(formatter.format(obj))
    }

    def read(json: JsValue): ZonedDateTime = json match {
      case JsString(s) => try {
        ZonedDateTime.parse(s, formatter)
      } catch {
        case t: Throwable => error(s)
      }
      case _ =>
        error(json.toString())
    }

    def error(v: Any): ZonedDateTime = {
      ZonedDateTime.now()
    }
  }

  implicit val salesByPeriodFormat = jsonFormat2(m.SalesByPeriod)
  implicit val salesByShopFormat = jsonFormat3(m.SalesByShop)
  implicit val salesByShopProductFormat = jsonFormat4(m.SalesByShopProduct)
  implicit val salesByShopPriceFormat = jsonFormat5(m.SalesByShopPrice)
  implicit val anyErrFormat = jsonFormat1(m.AnyErr)
  implicit val salesDataFormat = jsonFormat8(m.SalesData)
  implicit val salesDataListFormat = jsonFormat1(m.SalesResponces)
}
