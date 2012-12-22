package sabre.system

import akka.actor.{ Actor, ActorLogging }
import java.io.PrintWriter
import sabre.algorithm.AbstractResult

object ResultHandler {
  case class HandleResult(result: AbstractResult)
  case object AllResultsSent
}

class ResultHandler(outputFilename: String) extends Actor with ActorLogging {
  import ResultHandler._

  val outfile = new PrintWriter(outputFilename)

  override def receive = {
    case HandleResult(result) =>
      // log.info("Received result.")
      outfile.println(result)
    case AllResultsSent =>
      log.info("All results received, shutting down.")
      outfile.close()
      context.system.shutdown()
  }
}