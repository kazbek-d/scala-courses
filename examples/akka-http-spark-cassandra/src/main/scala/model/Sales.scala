package model

import java.time.ZonedDateTime

import com.github.nscala_time.time.Imports._


object Sales {
  import Utils.DateTimeUtils._
  case class SalesData(shop_id: Int, sale_date: ZonedDateTime, product_id: Int, product_count: Int, price: BigDecimal, category_id: Int, vendor_id: Int)
  case class SalesDataSQL(surrogate_pk: String, shop_id: Int, sale_date: DateTime, product_id: Int, product_count: Int, price: BigDecimal, category_id: Int, vendor_id: Int) {
    def toSalesData = SalesData(shop_id, sale_date.toZonedDateTime, product_id, product_count, price, category_id, vendor_id)
  }

  trait Requests
  case class SalesByPeriod(from: ZonedDateTime, to: ZonedDateTime) extends Requests
  case class SalesByShop(shop: List[Int], from: ZonedDateTime, to: ZonedDateTime) extends Requests
  case class SalesByShopProduct(shop: List[Int], products: List[Int], from: ZonedDateTime, to: ZonedDateTime) extends Requests
  case class SalesByShopPrice(shop: List[Int], price_from: BigDecimal, price_to: BigDecimal, from: ZonedDateTime, to: ZonedDateTime) extends Requests


  trait Responces
  trait Err extends Responces
  case class AnyErr(message: String) extends Err
  case class SalesResponces(data: List[SalesData]) extends Responces

}