package api


import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.AggEngineActor.ListEntertainmentByPageNo
import api.TvPageData.GetTvPageData
import model.EntertainmentResultJsonProtocol._
import model._
import util.ApplicationConfig._

import scala.concurrent.Future


object TvPageData {

  final object GetTvPageData

  def props(totalPageCount: Int): Props = Props(new TvPageData(totalPageCount))
}

class TvPageData(totalPageCount: Int) extends Actor with ActorLogging {
  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetTvPageData => {
      @annotation.tailrec
      def getTvPageData(total: Int, tvList: Future[List[Entertainment]]): Future[List[Entertainment]] = {
        total match {
          case x if (x == 0) => {
            tvList
          }
          case x => {
            log.info("TvData:- " + x)
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL$apiUriTvDiscover&api_key=$apiKey&page=$x"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[EntertainmentResult]
                  }
                  case _ => Future {
                    EntertainmentResult(0, 0, 0, List.empty[Entertainment])
                  }
                })
            getTvPageData(x - 1, httpResponse.flatMap(x => tvList.map(y => x.results ::: y)))
          }
        }
      }

      getTvPageData(totalPageCount, Future {
        List.empty[Entertainment]
      }).map(x => ListEntertainmentByPageNo(EntertainmentList(x), 2)).pipeTo(sender())
    }
  }
}