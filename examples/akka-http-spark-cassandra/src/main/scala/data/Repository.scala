package data

import model.Sales._

trait Repository {
  def getSalesByPeriod(condition: SalesByPeriod) : List[SalesData]
  def getSalesByShop(condition: SalesByShop) : List[SalesData]
  def getSalesByShopProduct(condition: SalesByShopProduct) : List[SalesData]
  def getSalesByShopPrice(condition: SalesByShopPrice) : List[SalesData]
}
