package sabre.system

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.remote.RemoteClientLifeCycleEvent
import com.typesafe.config.ConfigFactory
import java.net.InetAddress
import sabre.algorithm._
import sabre.system.Master._
import scala.Console.err

object Sabre {
  val sabreAkkaConfig = ConfigFactory.parseString("""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
        serialization-bindings {
          "scala.collection.Map" = java
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

  def execute(algorithm: AbstractAlgorithm, work: Iterable[Any]): Sabre =
    execute(algorithm, work, "SabreResult.txt")

  def execute(algorithm: AbstractAlgorithm, work: Iterable[Any], outputFilename: String): Sabre =
    execute(algorithm, work, new PlainTextOutputter(outputFilename))

  def execute(algorithm: AbstractAlgorithm, work: Iterable[Any], resultHandlerCreator: => Actor): Sabre =
    new Sabre(algorithm, work, resultHandlerCreator)
}

class Sabre private (
    algorithm: AbstractAlgorithm,
    work: Iterable[Any],
    resultHandlerCreator: => Actor) {
  import Sabre.sabreAkkaConfig
  val system = ActorSystem("Sabre", ConfigFactory.load(sabreAkkaConfig))

  val resultHandler = system.actorOf(Props(resultHandlerCreator), "resultHandler")
  val master = system.actorOf(Props(new Master(algorithm, resultHandler)), "master")

  system.eventStream.subscribe(master, classOf[RemoteClientLifeCycleEvent])

  err.println("Master deployed on " + InetAddress.getLocalHost().getHostName + " as " + master)

  work.map(DistributeWork(_)).foreach(master ! _)
  master ! AllWorkSent
}
