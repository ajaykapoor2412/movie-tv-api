package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.EntertainmentJsonProtocol._
import spray.json.DefaultJsonProtocol

case class EntertainmentResult(
                        page: Int,
                        total_results: Int,
                        total_pages: Int,
                        results: List[Entertainment]
                      )

object EntertainmentResultJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val entertainmentResultJsonFormat = jsonFormat4(EntertainmentResult.apply)
}
