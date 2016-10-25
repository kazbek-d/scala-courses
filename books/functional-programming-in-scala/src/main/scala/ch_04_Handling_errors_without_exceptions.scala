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


  println("mean of Seq() = " + mean(Seq()))
  println("mean of Seq(0) = " + mean(Seq(0)))
  println("mean of Seq(1, 2, 3) = " + mean(Seq(1, 2, 3)))

  val some1 = Some(1)
  val some33 = Some(33)
  val some5 = Some(5)
  val none = None

  def plus2(o: Option[Int]) = o.map(_ + 2)
  def lessThat10(o: Option[Int]) = o.filter(_ < 10)

  println("1 plus2 = " + plus2(some1))
  println("none plus2 = " + plus2(none))
  println("33 flatMap = " + some33.flatMap(Some(_)))
  println("5 lessThat10 = " + lessThat10(some5))
  println("none lessThat10 = " + lessThat10(none))
  println("none orElse 10 = " + none.orElse(Some(10)))
  println("none getOrElse 12 = " + none.getOrElse(12))


}
