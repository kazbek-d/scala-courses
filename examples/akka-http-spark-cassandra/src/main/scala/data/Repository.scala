package data

import model.Sales._

trait Repository {
  def getSalesByPeriod(condition: SalesByPeriod) : SalesResponces
  def getSalesByShop(condition: SalesByShop) : SalesResponces
  def getSalesByShopProduct(condition: SalesByShopProduct) : SalesResponces
  def getSalesByShopPrice(condition: SalesByShopPrice) : SalesResponces
}
