package api

import akka.actor.{Actor, ActorLogging, Props}
import api.MovieEngineActor.GetAllMovieCasts
import api.TvEngineActor.GetAllTvCasts
import model.{Cast, CastList}
import util.ExecutionContextService

object AggEngineActor {

  final case object GetDuplicateCasts

  def props(): Props = Props(new AggEngineActor())

}

class AggEngineActor() extends Actor with ActorLogging {
  implicit val ec = ExecutionContextService.createExecutionContext

  import AggEngineActor._

  var counter = 2
  var finalList = List.empty[Cast]
  val movieActor = context.actorOf(MovieEngineActor.props(self), "movieActor")
  val tvActor = context.actorOf(TvEngineActor.props(self), "tvActor")


  def receive: Receive = {
    case result@CastList(lists) => {
      counter match {
        case x if (x == 1) => {
          val result =
            for {
              l1 <- finalList
              l2 <- lists
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
          finalList = finalList ::: lists
          counter -= 1
        }
      }
    }

    case GetDuplicateCasts => {
      movieActor ! GetAllMovieCasts
      tvActor ! GetAllTvCasts
    }
  }
}

