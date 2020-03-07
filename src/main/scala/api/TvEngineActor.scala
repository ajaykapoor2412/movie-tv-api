package api

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.TvCastData.GetTvCastData
import api.TvEngineActor.{CastListByTvId, GetAllTvCasts, _}
import api.TvPageCount.GetTvCount
import api.TvPageData.GetTvPageData
import model._

object TvEngineActor {
  def props(actorRef: ActorRef) = Props(new TvEngineActor(actorRef))

  final case object GetAllTvCasts

  final case object GetTvTotalPageNo

  final case class GetAllTvList(totalPages: Int)

  final case class TvListByPageNo(tvs: TvList)

  final case class CastListByTvId(casts: CastList)

  final case class AllTvPageCount(pages: Int)

}

class TvEngineActor(actorRef: ActorRef) extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))


  override def receive: Receive = {

    case GetAllTvCasts => {
      log.info("GetAllTvCasts")
      val tvPageCountActor = context.actorOf(TvPageCount.props(), "tvPageCountActor")
      tvPageCountActor ! GetTvCount
    }
    case TvListByPageNo(casts) => {
      log.info("TvListByPageNo")
      val tvCastDataActor = context.actorOf(TvCastData.props(casts.list), "tvCastDataActor")
      tvCastDataActor ! GetTvCastData
    }

    case CastListByTvId(casts) => {
      log.info("CastListByTvId")
      actorRef ! casts
    }

    case AllTvPageCount(totalPages) => {
      val tvPageDataActor = context.actorOf(TvPageData.props(totalPages), "tvPageDataActor")
      tvPageDataActor ! GetTvPageData
    }

  }
}
