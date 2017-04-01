package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {

  test("predictTemperature 0") {
    val result = Visualization.predictTemperature(List.empty, Location(90.0, -176.0))
    assert(result === 0.0)
  }

  test("predictTemperature 10") {
    val result = Visualization.predictTemperature(List((Location(0.0, 0.0), 10.0)), Location(90.0, -176.0))
    assert(result === 10.0)
  }

  test("predictTemperature Zero distance") {
    val result = Visualization.predictTemperature(List((Location(0.0, 0.0), 10.0)), Location(0.0, 0.0))
    assert(result === 10.0)
  }

  test("predictTemperature Zero distance 2") {
    val result = Visualization.predictTemperature(List((Location(45.0, -90.0), 10.0), (Location(-45.0, 0.0), 20.0)), Location(45.0, -90.0))
    assert(result === 10.0)
  }


  test("interpolateColor") {
    val result = Visualization.interpolateColor(List((0, Color(0, 0, 0)), (20, Color(255, 255, 255))), 10)
    assert(result === Color(128, 128, 128))
  }

  test("interpolateColor 1") {
    val result = Visualization.interpolateColor(List((34.0, Color(255, 0, 0)), (2.147483647E9, Color(0, 0, 255))), value = 1.0737418405E9)
    assert(result === Color(128, 0, 128))
  }

  test("interpolateColor 2") {
    val result = Visualization.interpolateColor(List((-16.818225958207123, Color(255, 0, 0)), (-1.0, Color(0, 0, 255))), value = -26.818225958207123)
    assert(result === Color(255, 0, 0))
  }

  test("interpolateColor 3") {
    val result = Visualization.interpolateColor(List((-2.147483648E9, Color(255, 0, 0)), (0.0, Color(0, 0, 255))), value = -1.610612736E9)
    assert(result === Color(191, 0, 64))
  }

  test("interpolateColor 4") {
    val result = Visualization.interpolateColor(List((-10901.0, Color(255, 0, 0)), (1.0, Color(0, 0, 255))), value = -5450.0)
    assert(result === Color(128, 0, 128))
  }

  test("interpolateColor 5") {
    val result = Visualization.interpolateColor(List((-1439.0, Color(255, 0, 0)), (0.0, Color(0, 0, 255))), value = -719.5)
    assert(result === Color(128, 0, 128))
  }

  test("visualize") {
    val result = Visualization.visualize(
      List((Location(45.0, -90.0), -9.637508439737758)),
      List((-9.637508439737758, Color(255, 0, 0)), (-1.0, Color(0, 0, 255))))
    assert(1 === 1)
  }

  test("visualize 1") {
    val result = Visualization.visualize(
      List((Location(45.0,-90.0),-100.0), (Location(-45.0,0.0),100.0)),
      List((-100.0,Color(255,0,0)), (100.0,Color(0,0,255))))
    assert(1 === 1)
  }
}
