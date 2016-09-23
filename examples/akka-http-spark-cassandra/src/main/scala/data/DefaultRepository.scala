package data

import model.Sales._

class DefaultRepository extends Repository {
  override def getSalesByPeriod(condition: SalesByPeriod): List[SalesData] = ???
  override def getSalesByShop(condition: SalesByShop): List[SalesData] = ???
  override def getSalesByShopProduct(condition: SalesByShopProduct): List[SalesData] = ???
  override def getSalesByShopPrice(condition: SalesByShopPrice): List[SalesData] = ???
}