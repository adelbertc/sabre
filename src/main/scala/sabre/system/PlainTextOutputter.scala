package sabre.system

import akka.actor.{ Actor, ActorLogging }
import java.io.PrintWriter
import sabre.result.AbstractResult

class PlainTextOutputter(outputFilename: String) extends Actor with ActorLogging {
  import ResultHandler._

  val outfile = new PrintWriter(outputFilename)

  override def receive = {
    case HandleResult(result) => outfile.println(result)
    case AllResultsSent => outfile.close()
  }
}
