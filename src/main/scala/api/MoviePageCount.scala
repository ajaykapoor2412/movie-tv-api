package api

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.MoviePageCount.GetMovieCount
import model.EntertainmentResultJsonProtocol._
import model.{Entertainment, EntertainmentResult, PageCount}
import util.ApplicationConfig._

import scala.concurrent.Future

object MoviePageCount {

  final case object GetMovieCount

  def props() = Props(new MoviePageCount())
}

class MoviePageCount extends Actor {
  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetMovieCount => {
      val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL$apiUriMovieDiscover&api_key=$apiKey"))
        .flatMap(x => {
          x.status match {
            case StatusCodes.OK => {
              Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[EntertainmentResult]
            }
            case _ => Future {
              EntertainmentResult(0, 0, 0, List.empty[Entertainment])
            }
          }
        }
        )

      httpResponse.map(x => PageCount(x.total_pages, 1)).pipeTo(sender())
    }
  }
}
