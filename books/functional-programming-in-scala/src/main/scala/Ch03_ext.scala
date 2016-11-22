


object TestList extends App {

  def f(a: Int, b: Int): Int = a - b

  private val initialVal = 0

  import ListExample._

  private val list = List.apply(1, -2, 3)

  println(s"foldLeft    result: " + foldLeft(list, initialVal)(f))
  println("foldLeftByFoldRight: " + foldLeftByFoldRight(list, initialVal)(f))
  println(s"foldRight   result: " + foldRight(list, initialVal)(f))
  println("foldRightByFoldLeft: " + foldRightByFoldLeft(list, initialVal)(f))

}


object ListExample {

  sealed trait List[+A]

  case object Nil extends List[Nothing]

  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {
    def apply[A](xs: A*): List[A] =
      if (xs.isEmpty) Nil
      else Cons(xs.head, apply(xs.tail: _*))
  }

  @annotation.tailrec
  def foldLeft[A, B](xs: List[A], z: B)(f: (B, A) => B): B = xs match {
    case Nil => z
    case Cons(head, tail) => foldLeft(tail, f(z, head))(f)
  }

  @annotation.tailrec
  def foldRight[A, B](xs: List[A], z: B)(f: (A, B) => B): B = xs match {
    case Nil => z
    case Cons(head, tail) => foldRight(tail, f(head, z))(f)
  }

  def foldLeftByFoldRight[A, B](xs: List[A], z: B)(f: (B, A) => B): B = {
    def zFunc(b: B) = b
    def fFunc(a: A, rec: B => B): B => B =
      b => rec(f(b, a))
    foldRight(xs, zFunc(_))(fFunc)(z)
  }

  def foldRightByFoldLeft[A, B](xs: List[A], z: B)(f: (A, B) => B): B = {
    def zFunc(b: B) = b
    def fFunc(rec: B => B, a: A): B => B =
      b => rec(f(a, b))
    foldLeft(xs, zFunc(_))(fFunc)(z)
  }

}
