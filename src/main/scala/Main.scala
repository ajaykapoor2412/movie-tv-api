import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import api.AggEngineActor
import api.AggEngineActor.GetDuplicateCasts
import util.ExecutionContextService

object Main extends App {


  implicit val system: ActorSystem = ActorSystem("processingSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val ec = ExecutionContextService.createExecutionContext

  val aggEngineActor: ActorRef = system.actorOf(AggEngineActor.props(), "aggEngineActor")

  aggEngineActor ! GetDuplicateCasts
}