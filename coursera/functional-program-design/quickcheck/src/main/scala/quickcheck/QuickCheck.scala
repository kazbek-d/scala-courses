package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genMap: Gen[H] = for (k <- arbitrary[A]; m <- oneOf(const(empty), genMap)) yield insert(k,m)
  lazy val genHeap: Gen[H] = genMap
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  // If you insert an element into an empty heap, then find the minimum of the resulting heap, you get the element back.
  property("min1") = forAll { a: A =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  // If you insert any two elements into an empty heap, finding the minimum of the resulting heap should get the smallest of the two elements back.
  property("min2") = forAll { (a: A, b: A) =>
    val h: H = insert(b, insert(a, empty))
    findMin(h) == Math.min(a, b)
  }

  // If you insert an element into an empty heap, then delete the minimum, the resulting heap should be empty.
  property("delete") = forAll { a: A =>
    val h: H = deleteMin(insert(a, empty))
    isEmpty(h)
  }

  // Given any heap, you should get a sorted sequence of elements when continually finding and deleting minima.
  property("ordered") = forAll { h: H =>
    @annotation.tailrec
    def loop(acc: List[A], heep: H): List[A] = if (isEmpty(heep)) acc
    else loop(findMin(heep) :: acc, deleteMin(heep))
    val delList = loop(Nil, h)

    @annotation.tailrec
    def loop1(xs1: List[A], xs2: List[A]): Boolean = (xs1, xs2) match {
      case (Nil, Nil) => true
      case (_, Nil) => false
      case (Nil, _) => false
      case (h1 :: t1, h2 :: t2) => (h1 == h2) && loop1(t1, t2)
    }

    loop1(delList, delList.sorted)
  }

  // Finding a minimum of the melding of any two heaps should return a minimum of one or the other.
  property("meld") = forAll { (h1: H, h2: H) =>
    val h12 = meld(h1, h2)
    findMin(h12) == Math.min(findMin(h1), findMin(h2))
  }
  property("meld1") = forAll { (h1: H, h2: H, h3: H) =>
    val h12 = meld(h1, h2)
    val h123 = meld(h12, h3)
    findMin(h123) == Math.min(Math.min(findMin(h1), findMin(h2)), findMin(h3))
  }

}
