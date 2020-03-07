package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Cast(id: Int, name: String)


object CastJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val CastJsonFormat = jsonFormat2(Cast.apply)
}


