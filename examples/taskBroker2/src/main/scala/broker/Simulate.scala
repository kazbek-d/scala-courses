package broker

import java.time.LocalDateTime
import java.util.concurrent.Callable

import akka.actor.Props
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}




object Simulate extends App {
  println("************   Test Start   ************")
  println("        Press any key for exit")


  // Simulated data
  val r = scala.util.Random
  val data = (1 to 1000).map(increment => {
    val id = LocalDateTime.now plusSeconds increment
    def callable = new Callable[Unit] {
      override def call(): Unit = println(s" Task id: $id")
    }
    Task(id, callable)
  })


  // Implicits
  implicit val system = akka.actor.ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  // Run
  val source = Source.fromIterator(() => data.toIterator)
  val sink = Sink.actorRef(system.actorOf(Props(new TaskDispatcher)), Unit)
  source.runWith(sink)


  println("******************************************")
}


