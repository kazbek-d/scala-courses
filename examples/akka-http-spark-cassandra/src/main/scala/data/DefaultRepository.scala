package data

import com.datastax.spark.connector._
import model.Sales._
import org.apache.spark.{SparkConf, SparkContext}

// CQL Examples:
// CREATE TABLE salesdata (surrogate_pk varchar, shop_id int, sale_date timestamp, product_id int, product_count int, price double, category_id int, vendor_id int, PRIMARY KEY (surrogate_pk, shop_id, sale_date, product_id, price));
// INSERT INTO salesdata (surrogate_pk, shop_id, sale_date, product_id, product_count, price, category_id, vendor_id) VALUES ('2016-1-1', 2, '2016-01-01T03:00:00+03:00', 4, 5, 6.77, 7, 8);

// Docker:
// https://hub.docker.com/_/cassandra/

class DefaultRepository extends Repository {

  import Utils.DateTimeUtils._
  import Utils.RddUtils._

  lazy val conf = new SparkConf()
    .setAppName("Cassandra Demo")
    .setMaster("local[*]")
    .set("spark.cassandra.connection.host", "172.17.0.2")

  lazy val sc = new SparkContext(conf)
  lazy val rdd = sc.cassandraTable[SalesDataSQL]("test", "salesdata")

  override def getSalesByPeriod(condition: SalesByPeriod): SalesResponces = SalesResponces {
    toSalesDataSQL(rdd)
      .filter(row =>
        toSurrogate_pk_s(condition.from, condition.to).contains(row.surrogate_pk) &&
          row.sale_date.isAfter(condition.from.toDateTime) &&
          row.sale_date.isBefore(condition.to.toDateTime))
      .collect()
      .map(_.toSalesData).toList
  }

  override def getSalesByShop(condition: SalesByShop): SalesResponces = ???
  override def getSalesByShopProduct(condition: SalesByShopProduct): SalesResponces = ???
  override def getSalesByShopPrice(condition: SalesByShopPrice): SalesResponces = ???
}