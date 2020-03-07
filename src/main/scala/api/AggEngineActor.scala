package api

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.AggEngineActor.{GetDuplicateCasts, ListEntertainmentByPageNo}
import api.MovieCastData.GetMovieCastData
import api.MoviePageCount.GetMovieCount
import api.MoviePageData.GetMoviePageData
import api.TvCastData.GetTvCastData
import api.TvPageCount.GetTvCount
import api.TvPageData.GetTvPageData
import model._

object AggEngineActor {

  final case object GetDuplicateCasts

  def props(): Props = Props(new AggEngineActor())

  final case class ListEntertainmentByPageNo(eList: EntertainmentList, typeId: Int)

  final case class EntertainmentByPageNo(id: List[Int])

}

class AggEngineActor() extends Actor with ActorLogging {

  var allPageCountCounter = 2
  var allPageDataCounter = 2
  var allPageCastCounter = 2
  var listCastResult: List[Cast] = List.empty[Cast]
  var listPageCountTotal: List[PageCount] = List.empty[PageCount]
  var listEntertainmentByPageNo: List[ListEntertainmentByPageNo] = List.empty[ListEntertainmentByPageNo]

  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  def receive: Receive = {
    case PageCount(total, typeId) => {
      allPageCountCounter match {
        case x if (x == 1) => {
          listPageCountTotal = listPageCountTotal ::: List(PageCount(total, typeId))
          listPageCountTotal.map(x => {
            x.typeId match {
              case 1 => {
                log.info("Total MovieCount " + x.pages)
                val moviePageDataActor = context.actorOf(MoviePageData.props(x.pages), "moviePageDataActor")
                moviePageDataActor ! GetMoviePageData
              }
              case 2 => {
                log.info("Total TvCount " + x.pages)
                val tvPageDataActor = context.actorOf(TvPageData.props(x.pages), "tvPageDataActor")
                tvPageDataActor ! GetTvPageData
              }
              case _ => log.error("No matched typeId")
            }
          })
        }
        case x => {
          listPageCountTotal = listPageCountTotal ::: List(PageCount(total, typeId))
          allPageCountCounter -= 1
        }
      }
    }

    case ListEntertainmentByPageNo(eList, typeId) => {
      allPageDataCounter match {
        case x if (x == 1) => {
          listEntertainmentByPageNo = listEntertainmentByPageNo ::: List(ListEntertainmentByPageNo(eList, typeId))
          listEntertainmentByPageNo.map(x => {
            x match {
              case x if (x.typeId == 1) => {
                context.actorOf(MovieCastData.props(x.eList.list, x.eList.list.length), "moviePageCastActor") ! GetMovieCastData
              }
              case x if (x.typeId == 2) => {
                context.actorOf(TvCastData.props(x.eList.list, x.eList.list.length), "tvPageCastActor") ! GetTvCastData
              }
              case _ => log.error("No matched typeId")
            }
          })
        }
        case x => {
          listEntertainmentByPageNo = listEntertainmentByPageNo ::: List(ListEntertainmentByPageNo(eList, typeId))
          allPageDataCounter -= 1
        }
      }
    }
    case FinalCastResult(lists, total) => {
      allPageCastCounter match {
        case x if (x == 1) => {
          val result = for {
            l1 <- listCastResult
            l2 <- lists.cast
            if (l1.id == l2.id)
          } yield l1
          log.info("Total list of Actors and Actresses " + result.length)
          log.info("List of Actors and Actresses were in at least one movie and at least one tv episode in November 2019")
          log.info("========================")
          result.distinct.foreach(x => log.info("Name:- " + x.name + ", -------- CastId:- " + x.id))
          log.info("========================")
        }
        case x => {
          log.info("result1")
          listCastResult = listCastResult ::: lists.cast
          allPageCastCounter -= 1
        }
      }
    }

    case GetDuplicateCasts => {
      context.actorOf(MoviePageCount.props(), "moviePageCountActor") ! GetMovieCount
      context.actorOf(TvPageCount.props(), "tvPageCountActor") ! GetTvCount
    }
  }
}

