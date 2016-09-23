package common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol._


object implicits {
  private val actorSystemName: String = "sales-actor-system"

  implicit val system = ActorSystem(actorSystemName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}


object model_implicits {

  import model.{Sales => m}

  implicit val salesByPeriodFormat = jsonFormat2(m.SalesByPeriod)
  implicit val salesByShopFormat = jsonFormat3(m.SalesByShop)
  implicit val salesByShopProductFormat = jsonFormat4(m.SalesByShopProduct)
  implicit val salesByShopPriceFormat = jsonFormat5(m.SalesByShopPrice)
  implicit val anyErrFormat = jsonFormat1(m.AnyErr)
  implicit val salesDataFormat = jsonFormat7(m.SalesData)
}

