
import java.util.regex.Matcher

import Utilities._
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._


/** This "case class" lets us quickly define a complex type that contains a session length and a list of URL's visited,
  *  which makes up the session data state that we want to preserve across a given session. The "case class" automatically
  *  creates the constructor and accessors we want to use.
  */
case class SessionData(sessionLength: Long, clickstream: List[String], okCodes: Int, otherCodes: Int)


/** An example of using a State object to keep persistent state information across a stream. 
 *  In this case, we'll keep track of clickstreams on sessions tied together by IP addresses.
 */
object Sessionizer extends App {

  val tempFolder = "/home/kdzarasov/tmp/"

  /** This function gets called as new data is streamed in, and maintains state across whatever key you define. In this case,
    * it expects to get an IP address as the key, a String as a URL (wrapped in an Option to handle exceptions), and
    * maintains state defined by our SessionData class defined above. Its output is new key, value pairs of IP address
    * and the updated SessionData that takes this new line into account.
    */
  def trackStateFunc(batchTime: Time, ip: String, urlAndStatus: Option[(String, Int)], state: State[SessionData]): Option[(String, SessionData)] = {
    // Extract the previous state passed in (using getOrElse to handle exceptions)
    val previousState = state.getOption.getOrElse(SessionData(0, List(), 0, 0))


    // Create a new state that increments the session length by one, adds this URL to the clickstream, and clamps the clickstream 
    // list to 10 items
    val newState = SessionData(
      previousState.sessionLength + 1L,
      (previousState.clickstream :+ urlAndStatus.map(_._1).getOrElse("empty")).take(100),
      previousState.okCodes + urlAndStatus.map(_._2).count(x => x >= 200 && x < 300),
      previousState.otherCodes + urlAndStatus.map(_._2).count(x => !(x >= 200 && x < 300))
    )

    // Update our state with the new state.
    state.update(newState)

    // Return a new key/value result.
    Some((ip, newState))
  }

  // Needed only on Windows:
  //System.setProperty("spark.sql.warehouse.dir","file:///C:/temp")


  // Create the context with a 1 second batch size
  val ssc = new StreamingContext("local[*]", "Sessionizer", Seconds(1))

  setupLogging()

  // Construct a regular expression (regex) to extract fields from raw Apache log lines
  val pattern = apacheLogPattern()

  // We'll define our state using our trackStateFunc function above, and also specify a
  // session timeout value of 30 minutes.
  val stateSpec = StateSpec
    .function(trackStateFunc _)
    .timeout(Minutes(30))

  // Unix: nc -kl 9999 < access_log.txt -i 1
  // Create a socket stream to read log data published via netcat on port 9999 locally
  val lines = ssc.socketTextStream("127.0.0.1", 9999, StorageLevel.MEMORY_AND_DISK_SER)

  // Extract the (ip, url) we want from each log line
  val requests = lines.map(x => {
    val matcher: Matcher = pattern.matcher(x)
    if (matcher.matches()) {
      val ip = matcher.group(1)
      val request = matcher.group(5)
      val status = matcher.group(6).toInt
      val requestFields = request.toString.split(" ")
      val url = scala.util.Try(requestFields(1)) getOrElse "[error]"
      (ip, (url, status))
    } else ("error", ("error", 0))
  })

  // Now we will process this data through our StateSpec to update the stateful session data
  // Note that our incoming RDD contains key/value pairs of ip/URL, and that what our
  // trackStateFunc above expects as input.
  val requestsWithState = requests.mapWithState(stateSpec)

  // And we'll take a snapshot of the current state so we can look at it.
  val stateSnapshotStream = requestsWithState.stateSnapshots()

  // Process each RDD from each batch as it comes in
  stateSnapshotStream.foreachRDD((rdd, time) => {

    // We'll expose the state data as SparkSQL, but you could update some external DB
    // in the real world.

    // Get the singleton instance of SQLContext
    val sqlContext = SparkSession.builder().getOrCreate()
    //val sqlContext = SQLContextSingleton.getInstance(rdd.context)
    import sqlContext.implicits._

    // Slightly different syntax here from our earlier SparkSQL example. toDF can take a list
    // of column names, and if the number of columns matches what's in your RDD, it just works
    // without having to use an intermediate case class to define your records.
    // Our RDD contains key/value pairs of IP address to SessionData objects (the output from
    // trackStateFunc), so we first split it into 3 columns using map().
    val requestsDataFrame: Dataset[Row] =
        rdd.map(x => (x._1, x._2.sessionLength, x._2.clickstream, x._2.okCodes, x._2.otherCodes))
          .toDF("ip", "sessionLength", "clickstream", "okCodes", "otherCodes")
          .sort($"sessionLength".desc)

    // Create a SQL table from this DataFrame
    requestsDataFrame.createOrReplaceTempView("sessionData")

    // Dump out the results - you can do any SQL you want here.
    val sessionsDataFrame = sqlContext.sql("select * from sessionData")
    println(s"========= $time =========")
    sessionsDataFrame.show()

  })

  // Kick it off
  ssc.checkpoint(tempFolder + "checkpoint/")
  ssc.start()
  ssc.awaitTermination()
}



