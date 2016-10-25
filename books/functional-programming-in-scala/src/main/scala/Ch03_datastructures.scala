object Ch03_datastructures {

  sealed trait List[+A]
  case object Nil extends List[Nothing]
  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {
    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(x, xs) => x + sum(xs)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
      case Cons(x, xs) => x * product(xs)
    }

    def apply[A](as: A*): List[A] =
      if (as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))

    def tail[A](l: List[A]): List[A] = l match {
      case Nil => sys.error("tail on empty list")
      case Cons(x, y) => y
    }

    def setHead[A](l: List[A], h: A): List[A] = l match {
      case Nil => sys.error("setHead on empty list")
      case Cons(_, t) => Cons(h, t)
    }

    @annotation.tailrec
    def drop[A](l: List[A], n: Int): List[A] = if (n <= 0) l
    else l match {
      case Nil => Nil
      case Cons(_, t) => drop(t, n - 1)
    }

    @annotation.tailrec
    def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Cons(h, t) if f(h) => dropWhile(t, f)
      case _ => l
    }

    def dropWhile1[A](l: List[A])(f: A => Boolean): List[A] = l match {
      case Cons(h, t) if f(h) => dropWhile(t, f)
      case _ => l
    }

    def init[A](l: List[A]): List[A] = {
      @annotation.tailrec
      def loop(a1: List[A], a2: List[A]): List[A] = a1 match {
        case Nil => a2
        case Cons(h, Cons(h1, Nil)) => Cons(h, a2)
        case Cons(h, t) => loop(t, Cons(h, a2))
      }
      @annotation.tailrec
      def reverce(a1: List[A], a2: List[A]): List[A] = a1 match {
        case Nil => Nil
        case Cons(h, t) => reverce(t, Cons(h, a2))
      }
      reverce(loop(l, Nil), Nil)
    }

    def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B = as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

    def length[A](as: List[A]): Int =
      foldRight(as, 0)((_, b: Int) => b + 1)

    @annotation.tailrec
    def foldLeft[A, B](as: List[A], z: B)(f: (B, A) => B): B = as match {
      case Nil => z
      case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
    }

    def sum3(l: List[Int]): Int = foldLeft(l, 0)(_ + _)

    def prodict3(l: List[Double]): Double = foldLeft(l, 1.0)(_ * _)

    def count2[A](l: List[A]): Int = foldLeft(l, 0)((acc, _) => acc + 1)

    def reverce[A](l: List[A]): List[A] = foldLeft(l, List[A]())((acc, h) => Cons(h, acc))

    @annotation.tailrec
    def foldRight2[A, B](as: List[A], z: B)(f: (A, B) => B): B = as match {
      case Nil => z
      case Cons(x, xs) => foldRight2(xs, f(x, z))(f)
    }

    def append[A](l: List[A], r: List[A]): List[A] =
      foldRight2(l, r)(Cons(_, _))

    def concat[A](l: List[List[A]], r: List[A]): List[A] = {
      foldRight2(l, Nil: List[A])(append)
    }

    def increment(l: List[Int]): List[Int] = foldRight2(l, Nil: List[Int])((h, t) => Cons(h + 1, t))

    def stringer(l: List[Double]): List[String] = foldRight2(l, Nil: List[String])((h, t) => Cons(h.toString, t))

    def map[A, B](as: List[A])(f: A => B): List[B] = foldRight2(as, List[B]())((h, t) => Cons(f(h), t))

    def filter[A](l: List[A])(f: A => Boolean): List[A] = foldRight2(l, List[A]())((h, t) =>
      if (f(h)) Cons(h, t) else t)

    def flatMap[A, B](as: List[A])(f: A => List[B]): List[B] =
      foldRight2(as, List[B]())((h, t) => append(f(h), t))

    def filter2[A](l: List[A])(f: A => Boolean): List[A] =
      flatMap(l)(a => if (f(a)) Cons(a, Nil) else Nil)

    @annotation.tailrec
    def constuct[A,B](l:List[A],r:List[A], z:B)( f: (A, A, B) => B) : B = {
      l match {
        case Nil => z
        case Cons(hl, tl) => r match {
          case Nil => z
          case Cons(hr, tr) => constuct(tl, tr, f(hl, hr, z))(f)
        }
      }
    }

    def addPairwise(a: List[Int], b: List[Int]): List[Int] = (a,b) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(h1,t1), Cons(h2,t2)) => Cons(h1+h2, addPairwise(t1,t2))
    }

    def zipWith[A](l:List[A], r:List[A])(f: (A,A)=> A) : List[A] = (l,r) match {
      case (a, Nil) => Nil
      case (Nil, b) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(t1, t2)(f))
    }

    def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = {
      @annotation.tailrec
      def loop(l: List[A], r: List[A], acc: List[A]): List[A] = l match {
        case Nil => acc
        case Cons(h, t) => {
          r match {
            case Nil => acc
            case Cons(h2, t2) => loop(
              t,
              if (h == h2) t2 else Cons(h2, t2),
              if (h == h2) Cons(h, acc) else acc
            )
          }
        }
      }
      count2(loop(sup, sub, List[A]())) == count2(sub)
    }



  }

    sealed trait Tree[+A]
    case class Leaf[A](value: A) extends Tree[A]
    case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]



    def size[A](t: Tree[A]) : Int = t match {
      case Leaf(_) => 1
      case Branch(l, r) => 1 + size(l) + size(r)
    }

    def maxElem(t: Tree[Int]) : Int = t match {
      case Leaf(v) => v
      case Branch(l, r) => maxElem(l) max maxElem(r)
    }

  def depth[A](t: Tree[A]) : Int = t match {
    case Leaf(_) => 0
    case Branch(l, r) => 1 + (depth(l) max depth(r))
  }

  def mapt[A,B](t: Tree[A])( f: A => B) : Tree[B] = t match {
    case Leaf(a) => Leaf(f(a))
    case Branch(l, r) => Branch(mapt(l)(f), mapt(r)(f))
  }

  def fold[A,B](t: Tree[A])(f: A => B)(g: (B,B) => B): B = t match {
    case Leaf(a) => f(a)
    case Branch(l,r) => g(fold(l)(f)(g), fold(r)(f)(g))
  }

  def sizeViaFold[A](t: Tree[A]): Int =
    fold(t)(a => 1)(1 + _ + _)

  def maximumViaFold(t: Tree[Int]): Int =
    fold(t)(a => a)(_ max _)

  def depthViaFold[A](t: Tree[A]): Int =
    fold(t)(a => 0)((d1,d2) => 1 + (d1 max d2))


  def mapViaFold[A,B](t: Tree[A])(f: A => B): Tree[B] =
    fold(t)(a => Leaf(f(a)): Tree[B])(Branch(_,_))


  import List._

  def Ex_3_1() = {
    List(1,2,3,4,5) match {
      case Cons(x, Cons(2, Cons(4, _))) => x
      case Nil => 42
      case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
      case Cons(h, t) => h + sum(t)
      case _ => 101
    }
  }








  def Activate(): Unit = {
    val list = List(1, 2, 3)
    val l = List(1, 2, 3)
    val r = List(3, 4, 5, 6)
    val d = List(3.1, 4.7, 5.5, 6.9)
    val k = List(1, 10, 4, 17, 21, 2, 0, 4)


    println("ex_3_1 result: " + Ex_3_1.toString)
    println("ex_3_8 result: " + foldRight(list, List[Int]())(Cons(_, _)).toString)
    println("ex_3_9 result: " + length(list))
    println("ex_3_12 result: " + reverce(list).toString)
    println("ex_3_14 result: " + append(l, r).toString)
    println("ex_3_16 result: " + increment(l).toString)
    println("ex_3_17 result: " + stringer(d).toString)
    println("ex_3_19 result: " + filter(k)(_ % 2 != 0).toString)
    println("ex_3_20 result: " + flatMap(l)(i => List(i, i)).toString)
    println("ex_3_21 result: " + filter2(k)(_ % 2 != 0).toString)
    println("ex_3_22 result: " + constuct(l, r, List[Int]())((a, b, c) => Cons(a + b, c)).toString)
    println("ex_3_22_answers result: " + addPairwise(l, r).toString)
    println("ex_3_23 result: " + zipWith(l, r)(_ + _).toString)
    println("ex_3_24 result: " + hasSubsequence(l, List(2, 3)).toString)

    println("ex_3_25 result: " + size(Leaf(1)).toString)
    println("ex_3_25 result: " + size(Branch(Branch(Leaf(3), Leaf(4)), Leaf(2))).toString)
    println("ex_3_26 result: " + maxElem(Branch(Branch(Leaf(3), Leaf(4)), Leaf(2))).toString)
    println("ex_3_27 result: " + depth(Leaf(1)).toString)
    println("ex_3_27 result: " + depth(Branch(Branch(Leaf(3), Leaf(4)), Leaf(2))).toString)

    println("ex_3_27 result: " + mapt(Branch(Branch(Leaf(3), Leaf(4)), Leaf(2)))(_ * 2).toString)

  }
}
