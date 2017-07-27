import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object MapperApp extends App {

  val sc = new SparkContext(new SparkConf())
  Logger.getRootLogger.setLevel(Level.ERROR)

}
