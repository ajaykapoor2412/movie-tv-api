package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.CastJsonProtocol._
import spray.json.DefaultJsonProtocol

case class CastList(cast: List[Cast])

object CastListJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val CastListJsonFormat = jsonFormat1(CastList.apply)
}
