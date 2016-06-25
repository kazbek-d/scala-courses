package broker

import java.time.LocalDateTime
import java.util.concurrent.Callable


case class Task(id: LocalDateTime, callable: Callable[Unit])
case class Tick()
case class Stop()



trait TasksHeap extends BinomialHeap {
  implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isBefore _)

  override type A = Task

  override def ord = Ordering.by(_.id)
}


