package model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.CastResultJsonProtocol._
import spray.json.DefaultJsonProtocol

case class FinalCastResult(cast: CastResult, typeId: Int)

object FinalCastResultJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val finalCastResultJsonFormat = jsonFormat2(FinalCastResult.apply)
}
