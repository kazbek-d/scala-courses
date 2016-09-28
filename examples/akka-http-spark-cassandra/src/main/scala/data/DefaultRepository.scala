package data

import com.datastax.spark.connector._
import model.Sales._
import org.apache.spark.{SparkConf, SparkContext}

class DefaultRepository extends Repository {

  override def getSalesByPeriod(condition: SalesByPeriod): SalesResponces = {

    val conf = new SparkConf().setAppName("Cassandra Demo").setMaster("local[*]")
      .set("spark.cassandra.connection.host", "localhost")

    val sc = new SparkContext(conf)

    val rdd = sc.cassandraTable("test", "kv")
    println(rdd.count)
    println(rdd.first)
    println(rdd.map(_.getInt("value")).sum)
    SalesResponces(List())
  }
  override def getSalesByShop(condition: SalesByShop): SalesResponces = ???
  override def getSalesByShopProduct(condition: SalesByShopProduct): SalesResponces = ???
  override def getSalesByShopPrice(condition: SalesByShopPrice): SalesResponces = ???
}