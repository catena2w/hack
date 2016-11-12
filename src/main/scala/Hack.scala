import dispatch.{Http, url}
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Hack extends App with ScorexLogging {

  val Peer: String = "http://88.198.13.202:6869"
  val Headers: Map[String, String] = Map()
  //  val Headers: Map[String, String] = Map("api_key" -> "hsepassword")
  val InitialHeight = 1876
  val Confirmations = 1
  val Coeff: Double = 1
  //Соответствие аддреса и id токена, которым он оперирует
  val addressAssets: TrieMap[String, String] = TrieMap()

  @tailrec
  def loop(lastProcessedBlock: Int): Unit = {
    val myAddresses: Seq[String] = getRequest("/addresses").as[Seq[String]]

    //    Запрашиваем высоту блокчейна
    val height = (getRequest("/blocks/height") \ "height").as[Int]
    //    Запрашиваем блоки от последнего обработанного до Height-N
    (lastProcessedBlock until (height - Confirmations)) foreach { blockN =>
      log.info(s"Processing block $blockN")

      val block = getRequest(s"/blocks/at/$blockN")
      val transactionsJS = (block \ "transactions").as[List[JsValue]]
      transactionsJS.foreach { txJs =>
        //Ищем нововыпущенные токены с наших адресов
        if ((txJs \ "type").asOpt[Int].contains(3) &&
          (txJs \ "sender").asOpt[String].exists(s => myAddresses.contains(s))) {
          log.info(s"Found issue transaction $txJs")
          addressAssets.put((txJs \ "sender").as[String], (txJs \ "assetId").as[String])
        }
        //Ищем переводы на наш адрес
        if ((txJs \ "recipient").asOpt[String].exists(s => myAddresses.contains(s)) &&
          !(txJs \ "sender").asOpt[String].exists(s => myAddresses.contains(s))) {
          log.info(s"Received Waves $txJs")
          val myAddress = (txJs \ "recipient").as[String]
          val myAssetId = addressAssets.get(myAddress).get
          val amount = (txJs \ "amount").as[Long]
          val sender = (txJs \ "sender").as[String]
          val receivedAssetId = (txJs \ "assetId").asOpt[String]

          if (receivedAssetId.isEmpty) {
            //    Если нам пришли Waves – шлем в ответ (или на адрес из attachment) ассеты
            sendAsset((amount * Coeff).toInt, myAddress, sender, Some(myAssetId))
          }
        }
      }
    }

    Thread.sleep(10000)
    loop(height - Confirmations)
  }

  loop(InitialHeight)


  def sendAsset(amount: Int, myAddress: String, recipient: String, assetId: Option[String]): Unit = {
    val assetIdStr = assetId.map(a => "\"assetIdOpt\": \"" + a + "\",").getOrElse("")

    val json = "{\"recipient\": \"" + recipient + "\" " + assetIdStr + ", \"feeAmount\": 100000, \"amount\": " +
      amount + ", \"attachment\": \"base\", \"sender\": \"" + myAddress + "\"} "
    log.info("Transaction sended:" + postRequest("/assets/transfer", body = json))
  }

  def getRequest(us: String): JsValue = {
    val request = Http(url(Peer + us).GET <:< Headers)
    val response = Await.result(request, 10.seconds)
    Json.parse(response.getResponseBody)
  }


  def postRequest(us: String,
                  params: Map[String, String] = Map.empty,
                  body: String = ""): JsValue = {
    val request = Http(url(Peer + us).POST << params <:< Headers << body)
    val response = Await.result(request, 5.seconds)
    Json.parse(response.getResponseBody)
  }

}