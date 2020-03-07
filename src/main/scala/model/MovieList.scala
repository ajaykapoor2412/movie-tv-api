package model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.MovieJsonProtocol._
import spray.json.DefaultJsonProtocol

case class MovieList(list: List[Movie])

object MovieListJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val MovieListJsonFormat = jsonFormat1(MovieList.apply)
}
