package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Tv(id: Int, original_name: String)


object TvJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val TvJsonFormat = jsonFormat2(Tv.apply)
}