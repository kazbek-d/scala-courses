import java.io.{File, FileInputStream}
import java.net.URL

import SparkImplicits.spark
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.DataFrame
import org.apache.spark.{SparkConf, SparkContext}

object Beginner {

  def read(path: String): DataFrame = {
    val df: DataFrame = spark.read.format("CSV").option("header", "true").load(path)
    df.createOrReplaceTempView("df_csv")
    df
  }



  def process(path: String) = {



    //  import spark.implicits._
    //
    //  val df: DataFrame = rdd.select("store_id", "product_id", "sales_date", "supplier_id", "category_id", "product_count", "product_price").toDF()
    //  df.createOrReplaceTempView("df_sales")
    //
    //  // PARTITION BY with partitioning, ORDER BY, and window specification
    //  spark.sql("SELECT supplier_id, sales_date, SUM(product_price * product_count) OVER (PARTITION BY store_id, product_id ORDER BY supplier_id ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) FROM df_sales")
    //    .collect()
    //    .foreach(println)

  }

}
