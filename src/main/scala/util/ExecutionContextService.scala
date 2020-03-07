package util

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object ExecutionContextService {
  def createExecutionContext: ExecutionContext = {
    new ExecutionContext {
      val threadPool = Executors.newFixedThreadPool(1000)

      override def execute(runnable: Runnable) {
        threadPool.submit(runnable)
      }

      override def reportFailure(t: Throwable) {}

      def shutdown() = threadPool.shutdown();
    }
  }

}
