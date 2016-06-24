package broker

import java.time.LocalDateTime
import java.util.concurrent.Callable
import scala.concurrent.Future


case class Task(id: LocalDateTime, callable: Callable[Unit])



trait TasksHeap extends BinomialHeap {
  implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isBefore _)

  override type A = Task

  override def ord = Ordering.by(_.id)

  override def insert(x: A, ts: H) = super.insert(x, ts)
}



abstract class AbstractDispatcher {
  def Add(task: Task) : Unit
}
