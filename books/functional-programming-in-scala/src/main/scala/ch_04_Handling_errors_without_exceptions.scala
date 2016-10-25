package ch_04

sealed trait Option[+A] {
  def map[B](f: A => B): Option[B]
  def flatMap[B](f: A => Option[B]) : Option[B]
  def getOrElse[B >: A](default: => B) : B
  def orElse[B >: A](ob: => Option[B]) : Option[B]
  def filter(f: A => Boolean): Option[A]
}
case class Some[+A](get: A) extends Option[A] {
  override def map[B](f: (A) => B): Option[B] = this match {
    case Some(a) => Some(f(a))
    case _ => None
  }

  override def getOrElse[B >: A](default: => B): B = this match {
    case Some(a) => a
    case _ => default
  }

  override def flatMap[B](f: (A) => Option[B]): Option[B] = map(f) getOrElse None


  override def orElse[B >: A](ob: =>Option[B]): Option[B] = map(Some(_)) getOrElse ob

  override def filter(f: (A) => Boolean): Option[A] = this match {
    case Some(a) if f(a) => Some(a)
    case _ => None
  }
}

case object None extends Option[Nothing] {
  override def map[B](f: (Nothing) => B): Option[B] = None

  override def flatMap[B](f: (Nothing) => Option[B]): Option[B] = None

  override def getOrElse[B >: Nothing](default: => B): B = default

  override def orElse[B >: Nothing](ob: => Option[B]): Option[B] = ob

  override def filter(f: (Nothing) => Boolean): Option[Nothing] = None
}

object ch_04_Handling_errors_without_exceptions extends App {

  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)


  println(mean(Seq()))
  println(mean(Seq(0)))
  println(mean(Seq(1, 2, 3)))

  println(Some(1).map(_ + 2))
  println(Some(33).flatMap(Some(_)))
  println(Some(5).filter(_ > 100))
  println(None.filter(_ => false))
  println(None.orElse(Some(10)))
  println(None.getOrElse(12))


}
