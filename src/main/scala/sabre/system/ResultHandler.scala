package sabre.system

import akka.actor.{ Actor, ActorLogging }
import java.io.PrintWriter
import sabre.system.MasterResultHandlerProtocol._
import sabre.system.WorkerResultHandlerProtocol._

class ResultHandler(outputFilename: String) extends Actor with ActorLogging {
  val outfile = new PrintWriter(outputFilename)

  def receive = {
    case HandleResult(result) =>
      // log.info("Received result.")
      outfile.println(result)
    case AllResultsSent =>
      log.info("All results received, shutting down.")
      outfile.close()
      context.system.shutdown()
  }
}
