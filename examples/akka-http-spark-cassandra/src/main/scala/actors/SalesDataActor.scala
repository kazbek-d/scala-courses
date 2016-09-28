package actors

import akka.actor.{Actor, ActorLogging}
import data.Repository
import model.Sales._

class SalesDataActor(implicit repository: Repository) extends Actor with ActorLogging {

  def receive: Receive = {

    case request: SalesByPeriod => {
      log.info(s"SalesByPeriod comes.")
      sender ! repository.getSalesByPeriod(request)
    }
    case request: SalesByShop => {
      log.info(s"SalesByShop comes.")
      sender ! repository.getSalesByShop(request)
    }
    case request: SalesByShopProduct => {
      log.info(s"SalesByShopProduct comes.")
      sender ! repository.getSalesByShopProduct(request)
    }
    case request: SalesByShopPrice => {
      log.info(s"SalesByShopPrice comes.")
      sender ! repository.getSalesByShopPrice(request)
    }

  }
}