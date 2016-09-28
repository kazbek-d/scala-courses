import akka.http.scaladsl.model.StatusCodes
import com.m3.curly._
import data.FakeRepository
import model.{Sales => m}
import org.scalatest.{BeforeAndAfter, FunSuite}
import spray.json.JsonParser

class ServerTest extends FunSuite with BeforeAndAfter {

  implicit val repository = new FakeRepository
  val server = new Server()


  val ip = "localhost"
  val port = 8899
  server.bind(ip, port)

  import common.model_implicits._
  test("get-sales-by-period") {
    val body = "{\"from\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\", \"to\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\"}"
    val response = HTTP.post(s"http://$ip:$port/test/get-sales-by-period", body.getBytes, "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val responce = JsonParser(response.getTextBody).convertTo[m.SalesResponces]
    assert(responce.data.head.price === 100.500)
   }

  test("get-sales-by-shop") {
    val body = "{\"shop\": [1,2,3], \"from\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\", \"to\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\"}"
    val response = HTTP.post(s"http://$ip:$port/test/get-sales-by-shop", body.getBytes, "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val responce = JsonParser(response.getTextBody).convertTo[m.SalesResponces]
    assert(responce.data.length === 3)
    assert(responce.data.map(_.price).sum === 100.500 * 3)
  }

  test("get-sales-by-shop-product") {
    val body = "{\"shop\": [1,2,3], \"products\": [4,5,6], \"from\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\", \"to\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\"}"
    val response = HTTP.post(s"http://$ip:$port/test/get-sales-by-shop-product", body.getBytes, "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val responce = JsonParser(response.getTextBody).convertTo[m.SalesResponces]
    assert(responce.data.length === 9)
    assert(responce.data.map(_.price).sum === 100.500 * 9)
  }

  test("get-sales-by-shop-price") {
    val body = "{\"shop\": [1,2,3,4,5], \"price_from\": 1.11, \"price_to\": 4.44, \"from\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\", \"to\": \"2016-09-28T14:10:40+03:00[GMT+03:00]\"}"
    val response = HTTP.post(s"http://$ip:$port/test/get-sales-by-shop-price", body.getBytes, "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val responce = JsonParser(response.getTextBody).convertTo[m.SalesResponces]
    assert(responce.data.length === 5)
    assert(responce.data.map(_.price).sum === 5.55 * 5)

    server.unbind
  }

}