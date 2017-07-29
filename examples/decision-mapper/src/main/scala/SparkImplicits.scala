import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkImplicits {

  val conf = new SparkConf()
    .setAppName("SparkApp")
    .setMaster("local[*]")

  implicit val spark = SparkSession
    .builder()
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()

  Logger.getRootLogger.setLevel(Level.ERROR)


}
