package model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.TvJsonProtocol._
import spray.json.DefaultJsonProtocol

case class TvList(list: List[Tv] = List.empty)

object TvListJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val TvListJsonFormat = jsonFormat1(TvList.apply)
}

