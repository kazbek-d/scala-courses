import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import org.apache.spark.mllib.recommendation._

object MovieRecommendationsALS extends App {

  /** Load up a Map of movie IDs to movie names. */
  def loadMovieNames: Map[Int, String] = {

    // Handle character encoding issues:
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    Source.fromFile("./src/main/resources/ml-100k/u.item").getLines()
      .map(_.split('|'))
      .filter(_.length > 1)
      .map(f => f(0).toInt -> f(1).toString)
      .toMap
  }


  // Set the log level to only print errors
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create a SparkContext using every core of the local machine
  val sc = new SparkContext("local[*]", "MovieRecommendationsALS")

  println("Loading movie names...")
  val nameDict = loadMovieNames

  val data = sc.textFile("./src/main/resources/ml-100k/u.data")

  val ratings = data.map(x => x.split('\t')).map(x => Rating(x(0).toInt, x(1).toInt, x(2).toDouble)).cache()

  // Build the recommendation model using Alternating Least Squares
  println("\nTraining recommendation model...")

  val rank = 8
  val numIterations = 20

  val model = ALS.train(ratings, rank, numIterations)

  val userID = 0

  println("\nRatings for user ID " + userID + ":")

  val userRatings = ratings.filter(x => x.user == userID)

  val myRatings = userRatings.collect()

  for (rating <- myRatings) {
    println(nameDict(rating.product.toInt) + ": " + rating.rating.toString)
  }

  println("\nTop 10 recommendations:")

  val recommendations = model.recommendProducts(userID, 10)
  for (recommendation <- recommendations) {
    println(nameDict(recommendation.product.toInt) + " score " + recommendation.rating)
  }

}