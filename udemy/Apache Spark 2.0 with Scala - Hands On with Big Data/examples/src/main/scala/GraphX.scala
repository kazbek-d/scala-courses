import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import org.apache.log4j._
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._

/** Some examples of GraphX in action with the Marvel superhero dataset! */
object GraphX extends App {


  // Function to extract hero ID -> hero name tuples (or None in case of failure)
  def parseNames(line: String): Option[(VertexId, String)] =
  line.split('\"') match {
    case arr: Array[String] if arr.length > 1 =>
      val (vertexId, string) = (arr(0).trim().toLong, arr(1))
      if (vertexId < 6487) Some(vertexId, string) else None
    case _ => None
  }

  /** Transform an input line from marvel-graph.txt into a List of Edges */
  def makeEdges(line: String): List[Edge[Int]] = {
    val fields = line.split(" ")
    val origin = fields.head.toLong
    fields.map(x => Edge(origin, x.toLong, 0)).toList
  }

  // Set the log level to only print errors
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create a SparkContext using every core of the local machine
  val sc = new SparkContext("local[*]", "GraphX")

  // Build up our vertices
  val names = sc.textFile("./src/main/resources/additional/marvel-names.txt")
  val verts = names.flatMap(parseNames)

  // Build up our edges
  val lines = sc.textFile("./src/main/resources/additional/marvel-graph.txt")
  val edges = lines.flatMap(makeEdges)

  // Build up our graph, and cache it as we're going to do a bunch of stuff with it.
  val default = "Nobody"
  val graph = Graph(verts, edges, default).cache()

  // Find the top 10 most-connected superheroes, using graph.degrees:
  println("\nTop 10 most-connected superheroes:")
  // The join merges the hero names into the output; sorts by total connections on each node.
  graph.degrees.join(verts).sortBy(_._2._1, ascending = false).take(10).foreach(println)


  // Now let's do Breadth-First Search using the Pregel API
  println("\nComputing degrees of separation from SpiderMan...")

  // Start from SpiderMan
  val root: VertexId = 5306 // SpiderMan

  // Initialize each node with a distance of infinity, unless it's our starting point
  val initialGraph = graph.mapVertices((id, _) => if (id == root) 0.0 else Double.PositiveInfinity)

  // Now the Pregel magic
  val bfs = initialGraph.pregel(Double.PositiveInfinity, 10)(
    // Our "vertex program" preserves the shortest distance
    // between an inbound message and its current value.
    // It receives the vertex ID we are operating on,
    // the attribute already stored with the vertex, and
    // the inbound message from this iteration.
    (id, attr, msg) => math.min(attr, msg),

    // Our "send message" function propagates out to all neighbors
    // with the distance incremented by one.
    triplet => {
      if (triplet.srcAttr != Double.PositiveInfinity) {
        Iterator((triplet.dstId, triplet.srcAttr + 1))
      } else {
        Iterator.empty
      }
    },

    // The "reduce" operation preserves the minimum
    // of messages received by a vertex if multiple
    // messages are received by one vertex
    (a, b) => math.min(a, b)).cache()

  // Print out the first 100 results:
  bfs.vertices.join(verts).take(100).foreach(println)

  // Recreate our "degrees of separation" result:
  println("\n\nDegrees from SpiderMan to ADAM 3,031") // ADAM 3031 is hero ID 14
  bfs.vertices.filter(x => x._1 == 14).collect.foreach(println)

}