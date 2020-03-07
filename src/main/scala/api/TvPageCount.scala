package api

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.TvEngineActor.AllTvPageCount
import api.TvPageCount.GetTvCount
import model.TvResultJsonProtocol._
import model.{Tv, TvResult}
import util.ApplicationConfig._

import scala.concurrent.Future

object TvPageCount {

  final case object GetTvCount

  def props() = Props(new TvPageCount())
}

class TvPageCount extends Actor {
  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case GetTvCount => {
      val httpResponse = Http(context.system).singleRequest(HttpRequest(uri = s"$apiURL$apiUriTvDiscover&api_key=$apiKey"))
        .flatMap(x =>
          x.status match {
            case StatusCodes.OK => {
              Unmarshal(x.entity.withContentType(ContentTypes.`application/json`)).to[TvResult]
            }
            case _ => Future {
              TvResult(0, 0, 0, List.empty[Tv])
            }
          })

      httpResponse.map(x => AllTvPageCount(x.total_pages)).pipeTo(sender())
    }
  }
}