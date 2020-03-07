package api

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.MovieEngineActor.MovieListByPageNo
import api.MoviePageData.GetMoviePageData
import model.MovieResultJsonProtocol._
import model._
import util.ApplicationConfig._

import scala.concurrent.Future


object MoviePageData {

  final object GetMoviePageData

  def props(totalPageCount: Int): Props = Props(new MoviePageData(totalPageCount))
}

class MoviePageData(totalPageCount: Int) extends Actor {
  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetMoviePageData => {
      @annotation.tailrec
      def getMoviePageData(total: Int, movieList: Future[List[Movie]]): Future[List[Movie]] = {
        total match {
          case x if (x == 0) => {
            movieList
          }
          case x => {
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL$apiUriMovieDiscover&api_key=$apiKey&page=$x"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[MovieResult]
                  }
                  case _ => Future {
                    MovieResult(0, 0, 0, List.empty[Movie])
                  }
                })
            getMoviePageData(x - 1, httpResponse.flatMap(x => movieList.map(y => x.results ::: y)))
          }
        }
      }

      getMoviePageData(totalPageCount, Future {
        List.empty[Movie]
      }).map(x => MovieListByPageNo(MovieList(x))).pipeTo(sender())
    }
  }
}
