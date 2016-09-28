package data

import model.Sales._

class FakeRepository extends Repository {
  override def getSalesByPeriod(condition: SalesByPeriod) =
    SalesResponces {
      List(SalesData(1, condition.from, 2, 3, 100.500, 4, 5))
    }

  override def getSalesByShop(condition: SalesByShop) =
    SalesResponces {
      condition.shop.map(shop => SalesData(shop, condition.from, 2, 3, 100.500, 4, 5))
    }

  override def getSalesByShopProduct(condition: SalesByShopProduct) =
    SalesResponces {
      for {
        shop <- condition.shop
        product_id <- condition.products
      } yield {
        SalesData(shop, condition.from, product_id, 3, 100.500, 4, 5)
      }
    }

  override def getSalesByShopPrice(condition: SalesByShopPrice) =
    SalesResponces {
      condition.shop.map(shop => SalesData(shop, condition.from, 2, 3, condition.price_from + condition.price_to, 4, 5))
    }
}