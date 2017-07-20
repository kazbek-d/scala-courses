package broker

import java.time.LocalDateTime
import java.util.concurrent.Callable


case object Proceed

case class Task(id: LocalDateTime, callable: Callable[Unit])

