import dispatch.{Http, url}
import play.api.libs.json.{JsValue, Json}
import scorex.utils.ScorexLogging

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Hack extends App with ScorexLogging {

  val Peer: String = "http://88.198.13.202:6869"
  val Headers: Map[String, String] = Map()
  //  val Headers: Map[String, String] = Map("api_key" -> "hsepassword")
  val InitialHeight = 1000
  val N = 1
  val Coeff: Double = 1
  //Соответствие аддреса и it токена, которым он оперирует
  val addressAssets: TrieMap[String, String] = TrieMap()

  @tailrec
  def loop(lastProcessedBlock: Int): Unit = {
    val myAddresses: Seq[String] = ???

    //    Запрашиваем высоту блокчейна
    val height = (getRequest("/blocks/height") \ "height").as[Int]
    //    Запрашиваем блоки от последнего обработанного до Height-N
    (lastProcessedBlock until (height - N)) foreach { blockN =>
      log.info(s"Processing block $blockN")

      val block = getRequest(s"/blocks/at/$blockN")
      val transactionsJS = (block \ "transactions").as[List[JsValue]]
      transactionsJS.foreach { txJs =>
        //Ищем нововыпущенные токены
        if ((txJs \ "recipient"))

        if ((txJs \ "recipient").asOpt[String].contains(myAddress)) {
          val amount = (txJs \ "amount").as[Long]
          val sender = (txJs \ "sender").as[String]
          val receivedAssetId = (txJs \ "assetId").asOpt[String]
          log.info(s"Got transaction $txJs).asOpt[String]}")

          if (receivedAssetId.isEmpty) {
            //    Если нам пришли Waves – шлем в ответ (или на адрес из attachment) ассеты
            sendAsset((amount * Coeff).toInt, sender, Some(myAssetId))
          } else if (receivedAssetId.contains(myAssetId)) {
            //    Если пришли ассеты – шлем в ответ Waves
            sendAsset((amount * Coeff).toInt, sender, None)
          }
        }
      }
    }

    Thread.sleep(10000)
    loop(height - N)
  }

  loop(InitialHeight)


  def sendAsset(amount: Int, recepient: String, assetId: Option[String]): Unit = {
    val assetIdStr = assetId.map(a => "\"assetIdOpt\": \"" + a + "\",").getOrElse("")

    val json = "{\"recipient\": \"" + recepient + "\" " + assetIdStr + ", \"feeAmount\": 100000, \"amount\": " +
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