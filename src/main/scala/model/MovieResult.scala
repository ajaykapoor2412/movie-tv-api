package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.MovieJsonProtocol._
import spray.json.DefaultJsonProtocol

case class MovieResult(
                        page: Int,
                        total_results: Int,
                        total_pages: Int,
                        results: List[Movie]
                      )

object MovieResultJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieResultJsonFormat = jsonFormat4(MovieResult.apply)
}
