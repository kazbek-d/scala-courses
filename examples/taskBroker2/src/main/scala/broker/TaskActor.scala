package broker

import java.time.{LocalDateTime, ZoneOffset}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


class TaskActor(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  override def receive: Receive = {
    case task: Task =>

      context.system.scheduler.scheduleOnce(
        FiniteDuration(
          task.id.toInstant(ZoneOffset.UTC).toEpochMilli - LocalDateTime.now.toInstant(ZoneOffset.UTC).toEpochMilli, TimeUnit.MILLISECONDS),
        new Runnable {
          override def run(): Unit = {
            task.callable.call()
          }
        })
  }
}
