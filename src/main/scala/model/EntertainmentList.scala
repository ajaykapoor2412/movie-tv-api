package model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class EntertainmentList(list: List[Entertainment])

object EntertainmentListJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val entertainmentListJsonFormat = jsonFormat1(Entertainment.apply)
}
