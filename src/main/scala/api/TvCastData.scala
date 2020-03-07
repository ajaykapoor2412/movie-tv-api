package api


import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.TvCastData.GetTvCastData
import api.TvEngineActor.CastListByTvId
import model.CastResultJsonProtocol._
import model._
import util.ApplicationConfig.{apiKey, apiURL}

import scala.concurrent.Future

object TvCastData {

  final case object GetTvCastData

  def props(tvs: List[Tv]) = Props(new TvCastData(tvs))
}

class TvCastData(tvs: List[Tv]) extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetTvCastData => {
      @annotation.tailrec
      def getTvCastData(tvs: List[Tv], casts: Future[List[Cast]]): Future[List[Cast]] = {
        tvs match {
          case Nil => {
            casts
          }
          case h :: tail => {
            casts.map(x => log.info("Tv Casts " + x.length + " Tv count " + tail.length))
            val tvId = h.id
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL/tv/$tvId/credits?api_key=$apiKey"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[CastResult]
                  }
                  case _ => Future {
                    CastResult(List.empty[Cast])
                  }
                })
            getTvCastData(tail, httpResponse.flatMap(x => casts.map(y => x.cast ::: y)))
          }
        }
      }

      val castTvDataResult = getTvCastData(tvs, Future {
        List.empty[Cast]
      }).map(x => CastListByTvId(CastList(x))).pipeTo(sender())
    }
  }
}
