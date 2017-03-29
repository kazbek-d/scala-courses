package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {

  test("predictTemperature 0") {
    val result = Visualization.predictTemperature(List.empty, Location(90.0,-176.0))
    assert(result === 0.0)
  }

  test("predictTemperature 10") {
    val result = Visualization.predictTemperature(List((Location(0.0,0.0),10.0)), Location(90.0,-176.0))
    assert(result === 10.0)
  }

  test("predictTemperature Zero distance") {
    val result = Visualization.predictTemperature(List((Location(0.0,0.0),10.0)), Location(0.0,0.0))
    assert(result === 10.0)
  }

  test("predictTemperature Zero distance 2") {
    val result = Visualization.predictTemperature(List((Location(45.0,-90.0),10.0),(Location(-45.0,0.0),20.0)), Location(45.0,-90.0))
    assert(result === 10.0)
  }


}
