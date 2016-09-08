import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec

/** Find the movies with the most ratings. */
object PopularMoviesNicer extends App {

  /** Load up a Map of movie IDs to movie names. */
  def loadMovieNames: Map[Int, String] = {

    // Handle character encoding issues:
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    Source.fromFile("./src/main/resources/ml-100k/u.item").getLines()
      .map(_.split('|'))
      .filter(_.length > 1)
      .map(f=> f(0).toInt -> f(1).toString)
      .toMap
  }



  // Set the log level to only print errors
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create a SparkContext using every core of the local machine
  val sc = new SparkContext("local[*]", "PopularMoviesNicer")

  // Create a broadcast variable of our ID -> movie name map
  var nameDict = sc.broadcast(loadMovieNames)

  // Read in each rating line
  val lines = sc.textFile("./src/main/resources/ml-100k/u.data")

  // Map to (movieID, 1) tuples
  val movies = lines.map(x => (x.split("\t")(1).toInt, 1))

  // Count up all the 1's for each movie
  val movieCounts = movies.reduceByKey((x, y) => x + y)

  // Flip (movieID, count) to (count, movieID)
  val flipped = movieCounts.map(x => (x._2, x._1))

  // Sort
  val sortedMovies = flipped.sortByKey()

  // Fold in the movie names from the broadcast variable
  val sortedMoviesWithNames = sortedMovies.map(x => (nameDict.value(x._2), x._1))

  // Collect and print results
  val results = sortedMoviesWithNames.collect()

  results.foreach(println)

}

