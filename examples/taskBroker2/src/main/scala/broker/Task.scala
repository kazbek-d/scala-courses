package broker

import java.time.LocalDateTime
import java.util.concurrent.Callable


case class Task(id: LocalDateTime, callable: Callable[Unit])
