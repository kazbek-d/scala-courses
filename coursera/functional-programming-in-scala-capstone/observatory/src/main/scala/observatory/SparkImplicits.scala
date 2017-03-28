package observatory

import org.apache.log4j.{Level, Logger}

object SparkImplicits {

  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Observatory")
      .config("spark.master", "local")
      .config("spark.driver.memory", "1g")
      .config("spark.executor.memory", "1g")
      .getOrCreate()

  Logger.getRootLogger.setLevel(Level.ERROR)

}
