package broker

import java.util.concurrent.Callable

import scala.concurrent.Future
import scala.io.StdIn

object Simulate extends App with SimpleSimulate {
  println("************   Broker Start   ************")
  println("        Press any key for exit")

  StdIn.readLine()

  println("******************************************")
}









trait SimpleSimulate {

  import java.time.LocalDateTime
  import scala.concurrent

  import scalaz.Scalaz._
  import scalaz._


  object TaskDispatcher extends AbstractDispatcher with TasksHeap {

    override def Add(task: Task): Unit = {
      insert(task, empty)
    }
  }

  def currentTime: LocalDateTime = LocalDateTime.now()

  def task: Task = Task(currentTime, new Callable[Unit] {
    override def call(): Unit = {
      println(" Task id: " + currentTime)
    }
  })


  TaskDispatcher.Add(task)

}














