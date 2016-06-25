package broker

import java.time.LocalDateTime
import java.util.concurrent.Callable

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import scala.io.StdIn


object Simulate extends App {
  println("************   Broker Start   ************")
  println("        Press any key for exit")


  def currentTime: LocalDateTime = LocalDateTime.now()
  def newSimpleTask: Task = Task(currentTime, new Callable[Unit] {
    override def call(): Unit = {
      println(" Task id: " + currentTime)
    }
  })


  val system = akka.actor.ActorSystem("system")
  private val dispatcher = system.actorOf(Props[TaskDispatcher], "task-dispatcher")
  system.scheduler.schedule(0 seconds, 1 seconds, new Runnable {
    override def run(): Unit = {
      1 to 5 foreach { _ => dispatcher ! newSimpleTask }
    }
  })

  StdIn.readLine()

  dispatcher ! Stop
  system stop dispatcher
  system terminate

  println("******************************************")
}









//trait SimpleSimulate {

  class TaskDispatcher extends Actor with ActorLogging with TasksHeap {
    var h : H = empty
    private val worker = context.actorOf(Props[TaskWorker], "task-worker")

    override def receive: Receive = {
      case Stop => {
        log.info("Stop")
        context stop worker
        context stop self
      }
      case task@Task(_, _) => {
        log.info(task.id.toString)
        h = insert(task, h)
      }
      case Tick => {
        if(!isEmpty(h)) {
          log.info("tick")
          val min = findMin(h)
          worker ! min
          h = deleteMin(h)
        }
      }
    }
  }



  class TaskWorker extends Actor with ActorLogging {
    override def receive: Receive = {
      case Task(_, callable) => callable.call()
    }
  }
//}














