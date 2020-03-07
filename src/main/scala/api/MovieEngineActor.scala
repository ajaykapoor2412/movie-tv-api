package api

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import api.MovieCastData.GetMovieCastData
import api.MovieEngineActor._
import api.MoviePageCount.GetMovieCount
import api.MoviePageData.GetMoviePageData
import model._


object MovieEngineActor {
  def props(actorRef: ActorRef) = Props(new MovieEngineActor(actorRef))

  final case object GetAllMovieCasts

  final case object GetMovieTotalPageNo

  final case class GetAllMoviesList(totalPages: Int)

  final case class MovieListByPageNo(movies: MovieList)

  final case class CastListByMovieId(casts: CastList)

  final case class AllMoviePageCount(pages: Int)

}

class MovieEngineActor(actorRef: ActorRef) extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))


  override def receive: Receive = {
    case GetAllMovieCasts => {
      log.info("GetAllMovieCasts")
      val moviePageCountActor = context.actorOf(MoviePageCount.props(), "moviePageCountActor")
      moviePageCountActor ! GetMovieCount
    }
    case MovieListByPageNo(movies) => {
      log.info("MovieListByPageNo")
      val movieCastDataActor = context.actorOf(MovieCastData.props(movies.list), "movieCastDataActor")
      movieCastDataActor ! GetMovieCastData
    }

    case CastListByMovieId(casts) => {
      log.info("CastListByMovieId")
      actorRef ! casts
    }

    case AllMoviePageCount(totalPages) => {
      val moviePageDataActor = context.actorOf(MoviePageData.props(totalPages), "moviePageDataActor")
      moviePageDataActor ! GetMoviePageData
    }
  }
}
