package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Movie(id: Int, title: String)


object MovieJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val MovieJsonFormat = jsonFormat2(Movie.apply)
}

