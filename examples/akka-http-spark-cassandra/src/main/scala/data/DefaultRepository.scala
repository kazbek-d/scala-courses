package data

import java.time.ZonedDateTime

import com.datastax.spark.connector._
import com.github.nscala_time.time.Imports._
import model.Sales._
import org.apache.spark.{SparkConf, SparkContext}

object DateTimeUtils {

  implicit class ExtentionDateTime(val dt: DateTime) {
    def toZonedDateTime = dt.toGregorianCalendar.toZonedDateTime
  }

  implicit class ExtentionZonedDateTime(val zdt: ZonedDateTime) {
    def toTimestamp = zdt.toLocalDateTime
  }

  def toSurrogate_pk_s(from: ZonedDateTime, to: ZonedDateTime) =
    (0 to java.time.Period.between(from.toLocalDate, to.toLocalDate).getDays)
      .map(from.plusDays(_))
      .map(zdt => s"${zdt.getYear}-${zdt.getMonth}-${zdt.getDayOfMonth}").toList
}

class DefaultRepository extends Repository {

  // CREATE TABLE salesdata (surrogate_pk varchar, shop_id int, sale_date timestamp, product_id int, product_count int, price double, category_id int, vendor_id int, PRIMARY KEY (surrogate_pk, shop_id, sale_date, product_id, price));

  override def getSalesByPeriod(condition: SalesByPeriod): SalesResponces = {

    import DateTimeUtils._

    val conf = new SparkConf()
      .setAppName("Cassandra Demo")
      .setMaster("local[*]")
      .set("spark.cassandra.connection.host", "172.17.0.2")

    val sc = new SparkContext(conf)
    val rdd = sc.cassandraTable[SalesDataSQL]("test", "salesdata")

    SalesResponces {
      rdd.select("surrogate_pk", "shop_id", "sale_date", "product_id", "product_count", "price", "category_id", "vendor_id").take(100)
        .map(x =>
          SalesData(x.shop_id, x.sale_date.toZonedDateTime, x.product_id, x.product_count, x.price, x.category_id, x.vendor_id)).toList
    }
  }
  override def getSalesByShop(condition: SalesByShop): SalesResponces = ???
  override def getSalesByShopProduct(condition: SalesByShopProduct): SalesResponces = ???
  override def getSalesByShopPrice(condition: SalesByShopPrice): SalesResponces = ???
}