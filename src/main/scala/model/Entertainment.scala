package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Entertainment(id: Int)


object EntertainmentJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val entertainmentJsonFormat = jsonFormat1(Entertainment.apply)
}

