package sabre.system

import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import java.net.InetAddress
import sabre.algorithm._
import sabre.system.SabreMasterProtocol._
import scala.Console.err

object Sabre {
  val sabreAkkaConfig = ConfigFactory.parseString("""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
        serialization-bindings {
          "scalax.collection.Graph" = java
        }
      }
      remote {
        netty {
          hostname = """ + "\"" + InetAddress.getLocalHost().getHostName() + "\"" + """
          message-frame-size = 500 MiB
          port = 2554
        }
      }
    }
    """)
  def execute(algorithm: AbstractAlgorithm, work: Iterable[Any], outputFilename: String = "SabreResult.txt") =
    new Sabre(algorithm, work, outputFilename)
}

class Sabre(algorithm: AbstractAlgorithm, work: Iterable[Any], outputFilename: String = "SabreResult.txt") {
  import Sabre.sabreAkkaConfig
  val system = ActorSystem("Sabre", ConfigFactory.load(sabreAkkaConfig))

  val resultHandler = system.actorOf(Props(new ResultHandler(outputFilename)), "resultHandler")
  val master = system.actorOf(Props(new Master(algorithm, resultHandler)), "master")

  err.println("Master deployed on " + InetAddress.getLocalHost().getHostName + " as " + master)

  work.map(DistributeWork(_)).foreach(master ! _)
  master ! AllWorkSent
}
