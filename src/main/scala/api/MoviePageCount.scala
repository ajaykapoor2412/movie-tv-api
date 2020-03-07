package api

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.MovieEngineActor.AllMoviePageCount
import api.MoviePageCount.GetMovieCount
import model.MovieResultJsonProtocol._
import model.{Movie, MovieResult}
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
        .flatMap(x =>
          x.status match {
            case StatusCodes.OK => {
              Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[MovieResult]
            }
            case _ => Future {
              MovieResult(0, 0, 0, List.empty[Movie])
            }
          })

      httpResponse.map(x => AllMoviePageCount(x.total_pages)).pipeTo(sender())
    }
  }
}
