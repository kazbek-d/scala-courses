package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._

import ParallelParenthesesBalancing._

@RunWith(classOf[JUnitRunner])
class ParallelParenthesesBalancingSuite extends FunSuite {

  test("balance should work for empty string") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("", true)
  }

  test("balance should work for string of length 1") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("(", false)
    check(")", false)
    check(".", true)
  }

  test("balance should work for string of length 2") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("()", true)
    check(")(", false)
    check("((", false)
    check("))", false)
    check(".)", false)
    check(".(", false)
    check("(.", false)
    check(").", false)
  }



  test("balance should work for empty string parBalance") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, input.toArray.length * 2 / 3) == expected,
        s"balance($input) should be $expected")

    check("", true)
  }

  test("balance should work for string of length 1 parBalance") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, input.toArray.length * 2 / 3) == expected,
        s"balance($input) should be $expected")

    check("(", false)
    check(")", false)
    check(".", true)
  }

  test("balance should work for string of length 2 parBalance () t 1") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, 1) === expected,
        s"balance($input) should be $expected")

    check("()", true)
  }

  test("balance should work for string of length 2 parBalance") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, input.toArray.length * 2 / 3) == expected,
        s"balance($input) should be $expected")

    check("()", true)
    check(")(", false)
    check("((", false)
    check("))", false)
    check(".)", false)
    check(".(", false)
    check("(.", false)
    check(").", false)
  }

  test("balance should work for empty string parBalance ()()()() threshold 1") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, 1) == expected,
        s"balance($input) should be $expected")

//[Test Description] parBalance should invoke the parallel construct 7 times for string '()()()()' and threshold 1
//[Observed Error] 0 did not equal 7 The number of parallel calls should be 7

    check("()()()()", true)
  }

  test("balance should work for empty string parBalance (()) threshold 1") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, 1) == expected,
        s"balance($input) should be $expected")

    //[Test Description] parBalance should invoke the parallel construct 7 times for string '()()()()' and threshold 1
    //[Observed Error] 0 did not equal 7 The number of parallel calls should be 7

    check("(())", true)
  }

}