import scala.collection.+:

object Ch04_errorhandling {

  object OptEx {

    sealed trait Option[+A] {

      def map[B](f: A => B): Option[B] = this match {
        case None => None
        case Some(a) => Some(f(a))
      }

      def flatMap[B](f: A => Option[B]): Option[B] = this match {
        case None => None
        case Some(a) => f(a)
      }

      def getOrElse[B >: A](default: => B): B = this match {
        case None => default
        case Some(b) => b
      }

      def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
        case None => ob
        case _ => this
      }

      def filter(f: A => Boolean): Option[A] = this match {
        case Some(a) if f(a) => Some(a)
        case _ => None
      }
    }

    case class Some[+A](get: A) extends Option[A]

    case object None extends Option[Nothing]


    def variance(xs: Seq[Double]): Option[Double] = {
      @annotation.tailrec
      def loop(xs: Seq[Double], acc: Option[Double]): Option[Double] = xs match {
        case Nil => acc
        case h :: t => loop(t, acc.flatMap(m => Some(math.pow(h - m, 2))))
      }
      loop(xs, Some(0))
    }

    //  def variance1(xs: Seq[Double]): Option[Double] =
    //    mean(xs) flatMap (m => mean(xs.map(x => math.pow(x - m, 2))))

    def map2_my[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = (a, b) match {
      case (Some(a), Some(b)) => Some(f(a, b))
      case _ => None
    }

    def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] =
      a flatMap (pureA => b map (pureB => f(pureA, pureB)))

    def sequence_my[A](a: List[Option[A]]): Option[List[A]] = {
      @annotation.tailrec
      def loop[A](a: List[Option[A]], acc: Option[List[A]]): Option[List[A]] = a match {
        case None :: _ => None
        case Some(None) :: _ => None
        case Some(head) :: tail => loop(tail, acc.flatMap(list => Some(head :: list)))
        case _ => acc
      }
      loop(a, Some(List[A]()))
    }

    def sequence[A](a: List[Option[A]]): Option[List[A]] = a match {
      case Nil => Some(Nil)
      case head :: tail => {
        head match {
          case None => None
          case Some(None) => None
          case Some(pureHeat) => sequence(tail) map (pureList => pureHeat :: pureList)
        }
      }
    }

    def traverse_my[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
      sequence(a.map(f(_)))

    def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = a match {
      case Nil => Some(Nil)
      case head :: tail => traverse(tail)(f) flatMap (pureTail => f(head) map (pureHead => pureHead :: pureTail))
    }
  }

  object EitherEx {

    sealed trait Either[+E, +A] {
      def map[B](f: A => B): Either[E, B] = this match {
        case Left(a) => Left(a)
        case Right(a) => Right(f(a))
      }

      def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
        case Left(a) => Left(a)
        case Right(a) => f(a)
      }

      def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = this match {
        case Left(_) => b
        case Right(a) => Right(a)
      }

      //      def map_2_my[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = (this, b) match {
      //        case (Right(a), Right(b)) => Right(f(a, b))
      //        case _ => _
      //      }

      def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = for (
        aPure <- this;
        bPure <- b
      ) yield f(aPure, bPure)


      def leftMap[EB](f: E => EB): Either[EB, A] = this match {
        case Left(e) => Left(f(e))
        case Right(a) => Right(a)
      }

      def leftFlatMap[EB, AA >: A](f: E => Either[EB, AA]): Either[EB, AA] = this match {
        case Left(e) => f(e)
        case Right(a) => Right(a)
      }

      def map3[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C)(g: (E, EE) => EE): Either[EE, C] = (this, b) match {
        case (Left(a), Left(b)) => Left(g(a, b))
        case (Left(a), Right(_)) => Left(a)
        case (Right(_), Left(b)) => Left(b)
        case (Right(a), Right(b)) => Right(f(a, b))
      }

    }

    case class Left[+E](value: E) extends Either[E, Nothing]

    case class Right[+A](value: A) extends Either[Nothing, A]


    def sequence[E, A](es: List[Either[E, A]]): Either[E, List[A]] = es match {
      case Nil => Right(Nil)
      case head :: tail => sequence(tail) flatMap (pureTail => head map (pureHead => pureHead :: pureTail))
    }

    def traverse[E, A, B](as: List[A])(f: A => Either[E, B]): Either[E, List[B]] = as match {
      case Nil => Right(Nil)
      case head :: tail => traverse(tail)(f) flatMap (pureTail => f(head) map (pureHead => pureHead :: pureTail))
    }

    case class Person(name: Name, age: Age)

    sealed class Name(val value: String)

    sealed class Age(val value: Int)

    def mkName(name: String): Either[String, Name] =
      if (name == "" || name == null) Left("Name is empty.") else Right(new Name(name))

    def mkAge(age: Int): Either[String, Age] = if (age < 0) Left("Age is out of range.") else Right(new Age(age))

    def mkPerson(name: String, age: Int): Either[String, Person] =
      mkName(name)
        .flatMap(pureName =>
          mkAge(age).map(pureAge =>
            Person(pureName, pureAge)
          )
        )

    def mkPerson2(name: String, age: Int): Either[String, Person] =
      mkName(name).map3(mkAge(age))(Person(_, _))(_ + "   " + _)
  }

  object EitherPr {

    sealed trait Partial[+E, +A] {
//            def map[B](f: A => B): Partial[Seq[E], B] = this match {
//              case Left(head +: tail) => Left(Seq(head +: tail))
//              case Right(a) => Right(f(a))
//            }
      //
      //      def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
      //        case Left(a) => Left(a)
      //        case Right(a) => f(a)
      //      }
      //
      //      def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = this match {
      //        case Left(_) => b
      //        case Right(a) => Right(a)
      //      }
      //
      //      def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = for (
      //        aPure <- this;
      //        bPure <- b
      //      ) yield f(aPure, bPure)
      //
      //
      //      def leftMap[EB](f: E => EB): Either[EB, A] = this match {
      //        case Left(e) => Left(f(e))
      //        case Right(a) => Right(a)
      //      }
      //
      //      def leftFlatMap[EB, AA >: A](f: E => Either[EB, AA]): Either[EB, AA] = this match {
      //        case Left(e) => f(e)
      //        case Right(a) => Right(a)
      //      }
      //
      //      def map3[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C)(g: (E, EE) => EE): Either[EE, C] = (this, b) match {
      //        case (Left(a), Left(b)) => Left(g(a, b))
      //        case (Left(a), Right(_)) => Left(a)
      //        case (Right(_), Left(b)) => Left(b)
      //        case (Right(a), Right(b)) => Right(f(a, b))
      //      }

    }

    case class Left[+E](value: Seq[E]) extends Partial[Seq[E], Nothing]

    case class Right[+A](value: A) extends Partial[Nothing, A]

    case class Person(name: Name, age: Age)

    sealed class Name(val value: String)

    sealed class Age(val value: Int)

//    def mkName(name: String): Partial[Seq[String], Name] =
//      if (name == "" || name == null) Left("Name is empty.") else Right(new Name(name))
//
//    def mkAge(age: Int): Partial[Seq[String], Age] = if (age < 0) Left("Age is out of range.") else Right(new Age(age))

//    def mkPerson(name: String, age: Int): Partial[Seq[String], Person] =
//      mkName(name)
//        .flatMap(pureName =>
//          mkAge(age).map(pureAge =>
//            Person(pureName, pureAge)
//          )
//        )
//
//    def mkPerson2(name: String, age: Int): Partial[Seq[String], Person] =
//      mkName(name).map3(mkAge(age))(Person(_, _))(_ + "   " + _)
  }


}

object Activate_ch04 extends App {

  import Ch04_errorhandling.{OptEx => opt}
  import opt.{Some, None}
  import Ch04_errorhandling.{EitherEx => eth}
  import eth.{Right, Left}

  val seq = Seq(1.0, 2.0, 3.0)
  val list1 = List(Some(1), Some(2), Some(3))
  val list2 = List(Some(1), None, Some(3))
  val list3 = List(1, 2, 3)

  val listE1 = List(Right(1), Right(2), Right(3))
  val listE2 = List(Right(1), Left("NA"), Right(3))



  println("ex_4_2 result: " + opt.variance(seq))
  println("ex_4_4_sequence result: " + opt.sequence(list1))
  println("ex_4_4_sequence result: " + opt.sequence(list2))
  println("ex_4_4_sequence traverse: " + opt.traverse(list3)(x => Some(x + 2)))

  println("ex_4_7_sequence result: " + eth.sequence(listE1))
  println("ex_4_7_sequence result: " + eth.sequence(listE2))
  println("ex_4_7_sequence traverse: " + eth.traverse(list3)(x => Right(x * 2)))

  println("ex_4_8 mkPerson: " + eth.mkPerson2("Tom", 35))
  println("ex_4_8 mkPerson: " + eth.mkPerson2("", -1))
  println("ex_4_8 mkPerson: " + eth.mkPerson2("Bob", -1))
  println("ex_4_8 mkPerson: " + eth.mkPerson2("", 20))


}
