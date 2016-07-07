package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory

@RunWith(classOf[JUnitRunner]) 
class LineOfSightSuite extends FunSuite {
  import LineOfSight._
  test("lineOfSight should correctly handle an array of size 4") {
    val output = new Array[Float](4)
    lineOfSight(Array[Float](0f, 1f, 8f, 9f), output)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }


  test("upsweepSequential should correctly handle the chunk 1 until 4 of an array of 4 elements") {
    val res = upsweepSequential(Array[Float](0f, 1f, 8f, 9f), 1, 4)
    assert(res == 4f)
  }


  test("downsweepSequential should correctly handle a 4 element array when the starting angle is zero") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f), output, 0f, 1, 2)
    assert(output.toList == List(0f, 1f, 0f, 0f))
  }


  test("lineOfSight should correctly handle an array of size 4 parLineOfSight") {
    val output = new Array[Float](4)
    parLineOfSight(Array[Float](0f, 1f, 8f, 9f), output, 2)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  test("lineOfSight should correctly handle an array of size 4 parLineOfSight2") {
    val output = new Array[Float](4)
    parLineOfSight(Array[Float](0f, 1f, 8f, 9f), output, 1)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  test("downsweepSequential chunk") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f), output, 0f, 1, 2)
    assert(output.toList == List(0f, 1f, 0f, 0f))
  }
}



//[Test Description] parLineOfSight should invoke the parallel construct 30 times (15 times during upsweep and 15 times during downsweep) for an array of size 17, with threshold 1
//[Observed Error] 32 did not equal 30 (The number of parallel calls should be 30)
//[Lost Points] 3
//
//[Test Description] downsweep should correctly compute the output for a non-zero starting angle
//[Observed Error] List(0.0, 8.0, 7.0, 11.0, 12.0) did not equal List(0.0, 8.0, 8.0, 11.0, 12.0)
//[Lost Points] 2
//
//[Test Description] parLineOfSight should call parallel constuct 6 times, where the last two parallel constructs should update the 4 sections of the array (1 until 5), (5 until 9), (9 until 13), (13 until 17), respectively
//[Observed Error] success was false [During the execution of first part of 5th call to parallel construct, the indices 1 until 5 of the output array was not correctly updated]
//[Lost Points] 6