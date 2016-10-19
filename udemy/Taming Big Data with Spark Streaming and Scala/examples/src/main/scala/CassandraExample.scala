
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel

import java.util.regex.Pattern
import java.util.regex.Matcher

import Utilities._

import com.datastax.spark.connector._


// Docker
// https://hub.docker.com/_/cassandra/

// Cassandra CQL
// CREATE KEYSPACE "spark_test" WITH REPLICATION = { 'class' : 'SimpleStrategy' , 'replication_factor' :1 };
// CREATE TABLE spark_test.logtest (ip varchar, url varchar, status varchar, useragent varchar, PRIMARY KEY (ip));


/** Listens to Apache log data on port 9999 and saves URL, status, and user agent
 *  by IP address in a Cassandra database.
 */
object CassandraExample extends App {

  val tempFolder = "/home/kdzarasov/tmp/"

  // Set up the Cassandra host address
  val conf = new SparkConf()
  conf.set("spark.cassandra.connection.host", "172.17.0.2")
  conf.setMaster("local[*]")
  conf.setAppName("CassandraExample")

  // Create the context with a 10 second batch size
  val ssc = new StreamingContext(conf, Seconds(10))

  setupLogging()

  // Construct a regular expression (regex) to extract fields from raw Apache log lines
  val pattern = apacheLogPattern()

  // Unix: nc -kl 9999 < access_log.txt
  // Create a socket stream to read log data published via netcat on port 9999 locally
  val lines = ssc.socketTextStream("127.0.0.1", 9999, StorageLevel.MEMORY_AND_DISK_SER)

  // Extract the (IP, URL, status, useragent) tuples that match our schema in Cassandra
  val requests = lines.map(x => {
    val matcher: Matcher = pattern.matcher(x)
    if (matcher.matches()) {
      val ip = matcher.group(1)
      val request = matcher.group(5)
      val requestFields = request.toString.split(" ")
      val url = scala.util.Try(requestFields(1)) getOrElse "[error]"
      (ip, url, matcher.group(6).toInt, matcher.group(9))
    } else {
      ("error", "error", 0, "error")
    }
  })

  // Now store it in Cassandra
  requests.foreachRDD((rdd, time) => {
    rdd.cache()
    println("Writing " + rdd.count() + " rows to Cassandra")
    rdd.saveToCassandra("spark_test", "logtest", SomeColumns("ip", "url", "status", "useragent"))
  })

  // Kick it off
  ssc.checkpoint(tempFolder + "checkpoint/")
  ssc.start()
  ssc.awaitTermination()

}

