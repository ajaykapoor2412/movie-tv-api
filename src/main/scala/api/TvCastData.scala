package api


import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.TvCastData.GetTvCastData
import model.CastResultJsonProtocol._
import model._
import util.ApplicationConfig.{apiKey, apiURL}

import scala.concurrent.Future

object TvCastData {

  final case object GetTvCastData

  def props(ids: List[Entertainment], totalEntertainment: Int) = Props(new TvCastData(ids, totalEntertainment))
}

class TvCastData(ids: List[Entertainment], totalEntertainment: Int) extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetTvCastData => {
      @annotation.tailrec
      def getMoviePageData(listMovieId: List[Entertainment], movieList: Future[List[Cast]]): Future[List[Cast]] = {
        listMovieId match {
          case Nil => {
            movieList
          }
          case x :: tail => {
            movieList.map(x => log.info("TvCast:- " + x.length + " TvCount:- " + tail.length))
            val id = x.id
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL/tv/$id/credits?api_key=$apiKey"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[CastResult]
                  }
                  case _ => Future {
                    CastResult(List.empty[Cast])
                  }
                })
            getMoviePageData(tail, httpResponse.flatMap(x => movieList.map(y => x.cast ::: y)))
          }
        }
      }

      getMoviePageData(ids, Future {
        List.empty[Cast]
      }).map(x => FinalCastResult(CastResult(x), 1)).pipeTo(sender())
    }
  }
}
