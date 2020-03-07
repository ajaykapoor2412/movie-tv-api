package api

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.AggEngineActor.ListEntertainmentByPageNo
import api.MoviePageData.GetMoviePageData
import model.EntertainmentResultJsonProtocol._
import model._
import util.ApplicationConfig._

import scala.concurrent.Future


object MoviePageData {

  final object GetMoviePageData

  def props(totalPageCount: Int): Props = Props(new MoviePageData(totalPageCount))
}

class MoviePageData(totalPageCount: Int) extends Actor with ActorLogging {
  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetMoviePageData => {
      @annotation.tailrec
      def getMoviePageData(total: Int, movieList: Future[List[Entertainment]]): Future[List[Entertainment]] = {
        total match {
          case x if (x == 0) => {
            movieList
          }
          case x => {
            log.info("MovieData:- " + x)
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL$apiUriMovieDiscover&api_key=$apiKey&page=$x"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[EntertainmentResult]
                  }
                  case _ => Future {
                    EntertainmentResult(0, 0, 0, List.empty[Entertainment])
                  }
                })
            getMoviePageData(x - 1, httpResponse.flatMap(x => movieList.map(y => x.results ::: y)))
          }
        }
      }

      getMoviePageData(totalPageCount, Future {
        List.empty[Entertainment]
      }).map(x => ListEntertainmentByPageNo(EntertainmentList(x), 1)).pipeTo(sender())
    }
  }
}
