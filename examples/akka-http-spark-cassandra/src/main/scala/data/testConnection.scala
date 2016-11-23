package data

import java.time.ZonedDateTime
import java.util

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraTableScanRDD
import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.DateTime
import Utils.DateTimeUtils._
import com.datastax.driver.core.{ConsistencyLevel, ResultSet, Row}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import Utils.DateTimeUtils._
import com.datastax.spark.connector.util.ConfigParameter
import org.apache.spark.sql.catalyst.parser.SqlBaseParser.UnsupportedHiveNativeCommandsContext

case class site_activity(rowkey: String, sa_cid: String, sa_dl: String, sa_add_date: ZonedDateTime)

case class site_activity_sql(rowkey: String, sa_cid: String, sa_dl: Option[String], sa_add_date: DateTime) {
  def toSiteActivity = site_activity(rowkey, sa_cid, sa_dl.getOrElse(""), sa_add_date.toZonedDateTime)
}

case class site_activity_sql_justId(sa_cid: String)
case class site_activity_sql_justUrl(sa_cid: String, sa_dl: Option[String])


trait init {

  lazy val conf = new SparkConf()
    .setAppName("Cassandra Demo")
    .setMaster("local[*]") // spark://spark-master-dbd.qiwi.com:7077
    .set("spark.cassandra.connection.host", "spark-worker01-dbd.qiwi.com")
  //.set("spark.cassandra.input.consistency.level", ConsistencyLevel.LOCAL_QUORUM.toString)

  lazy val sc = new SparkContext(conf)

}

object testConnection extends App with init {

  lazy val rdd = sc.cassandraTable[site_activity_sql]("google_analytic", "sa_url")

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql]) = rdd.select("rowkey", "sa_cid", "sa_dl", "sa_add_date")

  toSiteActivitySQL(rdd).take(10).map(_.toSiteActivity).toList.foreach(println)
}

object testSimpleWhere extends App with init {

  lazy val rdd = sc.cassandraTable[site_activity_sql]("google_analytic", "sa_url")

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql]) = rdd.select("rowkey", "sa_cid", "sa_dl", "sa_add_date")

  private val sa_cid = "890192758.1474231975"
  toSiteActivitySQL(rdd).filter(_.sa_cid == sa_cid)
    .sortBy(_.sa_add_date)
    .take(100).map(_.toSiteActivity).toList.foreach(println)
}

object testSimpleGroup extends App with init {

  lazy val rdd = sc.cassandraTable[site_activity_sql]("google_analytic", "sa_url")

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql]) = rdd.select("rowkey", "sa_cid", "sa_dl", "sa_add_date")

  toSiteActivitySQL(rdd).groupBy(_.sa_cid).map(row => (row._1, row._2.size)).cache().take(1000).toList.foreach(println)
}

object testCount extends App with init {

  lazy val rdd = sc.cassandraTable[site_activity_sql]("google_analytic", "sa_url")

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql]) = rdd.select("rowkey", "sa_cid", "sa_dl", "sa_add_date")

  println(toSiteActivitySQL(rdd).cassandraCount())
}


object testHiveContext extends App with init {

  lazy val spark = SparkSession
    .builder()
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()

  lazy val rdd = spark.sparkContext.cassandraTable[site_activity_sql_justId]("google_analytic", "sa_url")

  import spark.implicits._

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql_justId]) = rdd.select("sa_cid")

  val df: DataFrame = toSiteActivitySQL(rdd).toDF()
  df.createOrReplaceTempView("df_sa_url")
  //  val result = spark.sql("SELECT * FROM df_sa_url LIMIT 100").collect()   // CQL like
  val result = spark.sql("FROM df_sa_url SELECT sa_cid LIMIT 100").collect()   // Hive like

  result.foreach(println)
}

object testHiveJoinContext extends App with init {

  lazy val spark = SparkSession
    .builder()
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()

  lazy val rdd1 = spark.sparkContext.cassandraTable[site_activity_sql_justId]("google_analytic", "sa_url")
  lazy val rdd2 = spark.sparkContext.cassandraTable[site_activity_sql_justUrl]("google_analytic", "sa_url")

  import spark.implicits._

  val df1: DataFrame = rdd1.select("sa_cid").toDF()
  df1.createOrReplaceTempView("df_sa_url1")
  val df2: DataFrame = rdd2.select("sa_cid", "sa_dl").toDF()
  df2.createOrReplaceTempView("df_sa_url2")

  val result = spark.sql("SELECT a.sa_cid, b.sa_dl FROM df_sa_url1 a JOIN df_sa_url2 b ON a.sa_cid = b.sa_cid LIMIT 100").collect()   // Hive Join

  result.foreach(println)
}

object testHiveWindowing extends App with init {

  // https://cwiki.apache.org/confluence/display/Hive/LanguageManual+WindowingAndAnalytics#LanguageManualWindowingAndAnalytics-EnhancementstoHiveQL

  lazy val spark = SparkSession
    .builder()
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()

  lazy val rdd = spark.sparkContext.cassandraTable[site_activity_sql_justUrl]("google_analytic", "sa_url")

  import spark.implicits._

  val df: DataFrame = rdd.select("sa_cid", "sa_dl").toDF()
  df.createOrReplaceTempView("df_sa_url")

  def print(rows : Array[org.apache.spark.sql.Row]) = rows.foreach(println)
  def testType(index: Int) = index match {

    // PARTITION BY with partitioning, ORDER BY, and window specification
    case 5 => print(spark.sql("SELECT sa_cid, COUNT(sa_dl) OVER (PARTITION BY sa_cid ORDER BY sa_dl ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) FROM df_sa_url").collect())

    // PARTITION BY with two partitioning columns, two ORDER BY columns, and no window specification
    case 4 => print(spark.sql("SELECT sa_cid, COUNT(sa_dl) OVER (PARTITION BY sa_cid, sa_dl ORDER BY sa_cid, sa_dl) FROM df_sa_url").collect())

    // PARTITION BY with one partitioning column, one ORDER BY column, and no window specification
    case 3 => print(spark.sql("SELECT sa_cid, COUNT(sa_dl) OVER (PARTITION BY sa_cid ORDER BY sa_dl) FROM df_sa_url").collect())

    // PARTITION BY with two partitioning columns, no ORDER BY or window specification
    case 2 => print(spark.sql("SELECT sa_cid, COUNT(sa_dl) OVER (PARTITION BY sa_cid, sa_dl) FROM df_sa_url").collect())

    // PARTITION BY with one partitioning column, no ORDER BY or window specification
    case 1 => print(spark.sql("SELECT sa_cid, COUNT(sa_dl) OVER (PARTITION BY sa_cid) FROM df_sa_url").collect())

    // Aggregate functions
    case _ => print(spark.sql("SELECT rank() OVER (ORDER BY count(sa_dl)) FROM df_sa_url GROUP BY sa_cid").collect())
  }

  testType(5)
}






object create_sa_url extends App with init {

  lazy val rdd = sc.cassandraTable[site_activity_sql]("google_analytic", "site_activity")

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql]) = rdd.select("rowkey", "sa_cid", "sa_dl", "sa_add_date")

  toSiteActivitySQL(rdd).connector.withSessionDo { session =>
    println(session.execute("CREATE TABLE google_analytic.sa_url (rowkey varchar, sa_cid varchar, sa_dl varchar, sa_add_date varchar, PRIMARY KEY (sa_cid, sa_add_date, rowkey))").all())
  }
}

object create_backup_restore extends App with init {

  lazy val rdd = sc.cassandraTable[site_activity_sql]("google_analytic", "site_activity")

  def toSiteActivitySQL(rdd: CassandraTableScanRDD[site_activity_sql]) = rdd.select("rowkey", "sa_cid", "sa_dl", "sa_add_date")

  toSiteActivitySQL(rdd).connector.withSessionDo { session =>
    val all1 = session.execute("COPY google_analytic.site_activity (rowkey, sa_cid, sa_dl, sa_add_date) TO 'temp_001.csv')").all()
    val all2 = session.execute("COPY google_analytic.sa_url (rowkey, sa_cid, sa_dl, sa_add_date) FROM 'temp_001.csv')").all()
    println(all1)
    println(all2)
  }

}

















