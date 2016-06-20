package patmat

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {
	trait TestTrees {
		val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
		val t2 = Fork(Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5), Leaf('d',4), List('a','b','d'), 9)
	}


  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }


  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a','b','d'))
    }
  }


  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }


  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e',1), Leaf('t',2), Leaf('x',3)))
  }


  test("combine of some leaf list") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(combine(leaflist) === List(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4)))
  }


  test("decode and encode a very short text should be identity") {
    new TestTrees {
      private val enc = encode(t1)("ab".toList)
      private val dec = decode(t1, enc)
      assert(dec === "ab".toList)
    }
  }


  test("decode and quickEncode a very short text should be identity") {
    new TestTrees {
      private val enc = quickEncode(t1)("ab".toList)
      private val dec = decode(t1, enc)
      assert(dec === "ab".toList)
    }
  }

  test("2 chars phrase") {
    new TestTrees {
      val txt = "babab"
      val chars = string2Chars(txt)
      val tree = createCodeTree(chars)
      private val enc = encode(tree)(chars)
      private val dec = decode(tree, enc)
      assert(dec === chars)
    }
  }

  test("3 chars phrase") {
    new TestTrees {
      val txt = "abc"
      val chars = string2Chars(txt)
      val tree = createCodeTree(chars)
      private val enc = encode(tree)(chars)
      private val dec = decode(tree, enc)
      assert(dec === chars)
    }
  }

  test("3 chars phrase more chars") {
    new TestTrees {
      val txt = "cbababc"
      val chars = string2Chars(txt)
      val tree = createCodeTree(chars)
      private val enc = encode(tree)(chars)
      private val dec = decode(tree, enc)
      assert(dec === chars)
    }
  }

  test("more complicated test (Encode)") {
    new TestTrees {
      val txt = "Attention: Once you have submitted your solution."
      val chars = string2Chars(txt)
      val tree = createCodeTree(chars)
      private val enc = encode(tree)(chars)
      private val dec = decode(tree, enc)
      assert(dec === chars)
    }
  }

  test("more complicated test (quickEncode)") {
    new TestTrees {
      val txt = "Attention: Once you have submitted your solution, you should see your grade and a feedback about your code on the Coursera website within 10 minutes. If you want to improve your grade, just submit an improved solution."
      val chars = string2Chars(txt)
      val tree = createCodeTree(chars)
      private val enc = quickEncode(tree)(chars)
      private val dec = decode(tree, enc)
      assert(dec === chars)
    }
  }
}
