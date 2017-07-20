package broker

import java.time.LocalDateTime


trait TasksHeap extends BinomialHeap {
  implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isBefore _)

  override type A = Task

  override def ord = Ordering.by(_.id)
}
