package Utils

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraTableScanRDD
import model.Sales.SalesDataSQL


object RddUtils {

    def toSalesDataSQL(rdd: CassandraTableScanRDD[SalesDataSQL]) =
      rdd.select("surrogate_pk", "shop_id", "sale_date", "product_id", "product_count", "price", "category_id", "vendor_id")

}
