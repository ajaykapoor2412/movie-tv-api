package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.CastJsonProtocol._
import spray.json.DefaultJsonProtocol

case class CastResult(cast: List[Cast])

object CastResultJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val castResultJsonFormat = jsonFormat1(CastResult.apply)
}