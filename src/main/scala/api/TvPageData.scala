package api


import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.TvEngineActor.TvListByPageNo
import api.TvPageData.GetTvPageData
import model.TvResultJsonProtocol._
import model._
import util.ApplicationConfig._

import scala.concurrent.Future


object TvPageData {

  final object GetTvPageData

  def props(totalPageCount: Int): Props = Props(new TvPageData(totalPageCount))
}

class TvPageData(totalPageCount: Int) extends Actor {
  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetTvPageData => {
      @annotation.tailrec
      def getTvPageData(total: Int, tvList: Future[List[Tv]]): Future[List[Tv]] = {
        total match {
          case x if (x == 0) => {
            tvList
          }
          case x => {
            val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL$apiUriTvDiscover&api_key=$apiKey&page=$x"))
              .flatMap(x =>
                x.status match {
                  case StatusCodes.OK => {
                    Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[TvResult]
                  }
                  case _ => Future {
                    TvResult(0, 0, 0, List.empty[Tv])
                  }
                })
            getTvPageData(x - 1, httpResponse.flatMap(x => tvList.map(y => x.results ::: y)))
          }
        }
      }

      getTvPageData(totalPageCount, Future {
        List.empty[Tv]
      }).map(x => TvListByPageNo(TvList(x))).pipeTo(sender())
    }
  }
}