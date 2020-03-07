package model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.TvJsonProtocol._
import spray.json.DefaultJsonProtocol

case class TvResult(
                     page: Int,
                     total_results: Int,
                     total_pages: Int,
                     results: List[Tv]
                   )

object TvResultJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val TvResultJsonFormat = jsonFormat4(TvResult.apply)
}

