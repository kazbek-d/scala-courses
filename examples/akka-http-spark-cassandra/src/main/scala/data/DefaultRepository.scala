package data

import com.datastax.spark.connector._
import model.Sales._
import org.apache.spark.{SparkConf, SparkContext}

class DefaultRepository extends Repository {

  // CREATE TABLE salesdata (surrogate_pk varchar, shop_id int, sale_date timestamp, product_id int, product_count int, price double, category_id int, vendor_id int, PRIMARY KEY (surrogate_pk, shop_id, sale_date, product_id, price));

  override def getSalesByPeriod(condition: SalesByPeriod): SalesResponces = {

    val conf = new SparkConf()
      .setAppName("Cassandra Demo")
      .setMaster("local[*]")
      .set("spark.cassandra.connection.host", "172.17.0.2")

    val sc = new SparkContext(conf)

    val rdd = sc.cassandraTable[SalesData]("test", "salesdata")
    println(rdd.count)
    println(rdd.first)

    SalesResponces {
      rdd.select("surrogate_pk", "shop_id", "sale_date", "product_id", "product_count", "price", "category_id", "vendor_id").take(100).toList
    }
  }
  override def getSalesByShop(condition: SalesByShop): SalesResponces = ???
  override def getSalesByShopProduct(condition: SalesByShopProduct): SalesResponces = ???
  override def getSalesByShopPrice(condition: SalesByShopPrice): SalesResponces = ???
}