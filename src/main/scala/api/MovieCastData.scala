package api

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.MovieCastData.GetMovieCastData
import api.MovieEngineActor.CastListByMovieId
import model.CastResultJsonProtocol._
import model.{Cast, CastList, CastResult, Movie}
import util.ApplicationConfig.{apiKey, apiURL}

import scala.concurrent.Future

object MovieCastData {

  final case object GetMovieCastData

  def props(movies: List[Movie]) = Props(new MovieCastData(movies))
}

class MovieCastData(movies: List[Movie]) extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetMovieCastData => {
      @annotation.tailrec
      def getMovieCastData(movieList: List[Movie], casts: Future[List[Cast]]): Future[List[Cast]] = {
        movieList match {
          case Nil => {
            casts
          }
          case h :: tail => {
            casts.map(x => log.info("Movie Casts " + x.length + " Movie Count " + tail.length))
            val movieId = h.id
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL/movie/$movieId/credits?api_key=$apiKey"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[CastResult]
                  }
                  case _ => Future {
                    CastResult(List.empty[Cast])
                  }
                })
            getMovieCastData(tail, httpResponse.flatMap(x => casts.map(y => x.cast ::: y)))
          }
        }
      }

      val castMovieDataResult = getMovieCastData(movies, Future {
        List.empty[Cast]
      }).map(x => CastListByMovieId(CastList(x))).pipeTo(sender())
    }
  }
}
