import akka.http.scaladsl.model.StatusCodes
import com.m3.curly._
import model.{Transfer => m}
import org.scalatest.{BeforeAndAfter, FunSuite}
import spray.json.JsonParser

class ServerTest extends FunSuite with BeforeAndAfter with Server {

  val ip = "localhost"
  val port = 8899
  bind(ip, port)

  import common.model_implicits._
  test("Deposit") {
    val body = "{\"acc\": \"40101810020000000001\", \"amount\": 23.15}"
    val response = HTTP.post(s"http://$ip:$port/transfer/Deposit", body.getBytes, "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val balance = JsonParser(response.getTextBody).convertTo[m.Balance]
    assert(balance.amount === 23.15)
   }

  test("GetBalance") {
    val body = "{\"acc\": \"40101810020000000001\"}"
    val response = HTTP.post(s"http://$ip:$port/transfer/GetBalance", body.getBytes(), "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val balance = JsonParser(response.getTextBody).convertTo[m.Balance]
    assert(balance.amount === 23.15)
  }

  test("Withdraw") {
    val body = "{\"acc\": \"40101810020000000001\", \"amount\": 20.15}"
    val response = HTTP.post(s"http://$ip:$port/transfer/Withdraw", body.getBytes(), "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val balance = JsonParser(response.getTextBody).convertTo[m.Balance]
    assert(balance.amount === 3)
    val response2 = HTTP.post(s"http://$ip:$port/transfer/Withdraw", body.getBytes(), "application/json")
    assert(StatusCodes.getForKey(response2.getStatus).get === StatusCodes.BadRequest)
  }

  test("Transfer2Acc") {
    val transferBody = "{\"acc1\": \"40101810020000000001\", \"acc2\": \"40101810020000000002\", \"amount\": 1.15}"
    val getbalanceBody = "{\"acc\": \"40101810020000000002\"}"
    val response = HTTP.post(s"http://$ip:$port/transfer/Transfer2Acc", transferBody.getBytes(), "application/json")
    assert(StatusCodes.getForKey(response.getStatus).get === StatusCodes.Accepted)
    val balance = JsonParser(response.getTextBody).convertTo[m.Balance]
    assert(balance.amount === 1.85)
    val response2 = HTTP.post(s"http://$ip:$port/transfer/GetBalance", getbalanceBody.getBytes(), "application/json")
    assert(StatusCodes.getForKey(response2.getStatus).get === StatusCodes.Accepted)
    val balance2 = JsonParser(response2.getTextBody).convertTo[m.Balance]
    assert(balance2.amount === 1.15)

    unbind
  }

}