package data

import model.Sales._

class DefaultRepository extends Repository {
  override def getSalesByPeriod(condition: SalesByPeriod): SalesResponces = ???
  override def getSalesByShop(condition: SalesByShop): SalesResponces = ???
  override def getSalesByShopProduct(condition: SalesByShopProduct): SalesResponces = ???
  override def getSalesByShopPrice(condition: SalesByShopPrice): SalesResponces = ???
}