package broker

import java.time.{LocalDateTime, ZoneOffset}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


class TaskDispatcher(implicit ec: ExecutionContext) extends Actor with ActorLogging with TasksHeap {

  val me = self

  var h: H = empty

  @tailrec
  private def proceed : Unit =
    if (isEmpty(h)) {
      // log.info("tick. empty head")
    } else {
      val min: Task = findMin(h)
      val now = LocalDateTime.now
      if (min.id.isBefore(now)) {
        min.callable.call()
        h = deleteMin(h)
        proceed
      } else {
        context.system.scheduler.scheduleOnce(
          FiniteDuration(
            min.id.toInstant(ZoneOffset.UTC).toEpochMilli - LocalDateTime.now.toInstant(ZoneOffset.UTC).toEpochMilli, TimeUnit.MILLISECONDS),
          new Runnable {
            override def run(): Unit = {
              me ! Proceed
            }
          })
      }
    }


  override def receive: Receive = {

    case task: Task =>
      h = insert(task, h)
      proceed

    case Proceed =>
      proceed

  }

}
