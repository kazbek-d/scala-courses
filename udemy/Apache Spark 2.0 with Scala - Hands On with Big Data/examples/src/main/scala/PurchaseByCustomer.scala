import org.apache.log4j._
import org.apache.spark._

/** Compute the average number of friends by age in a social network. */
object PurchaseByCustomer extends App {

  /** A function that splits a line of input into (age, numFriends) tuples. */
  def parseLine(line: String) = {
    // Split by commas
    val fields = line.split(",")
    // Extract the age and numFriends fields, and convert to integers
    val customer = fields(0).toInt
    val purchase = fields(2).toFloat
    // Create a tuple that is our result.
    (customer, purchase)
  }


  // Set the log level to only print errors
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create a SparkContext using every core of the local machine
  val sc = new SparkContext("local[*]", "PurchaseByCustomer")

  // Load each line of the source data into an RDD
  val lines = sc.textFile("./src/main/resources/additional/customer-orders.csv")

  // Use our parseLines function to convert to (age, numFriends) tuples
  val rdd = lines.map(parseLine)

  val customersPurchase = rdd.reduceByKey(_ + _).sortByKey().collect()

  customersPurchase.foreach(println)

}
  