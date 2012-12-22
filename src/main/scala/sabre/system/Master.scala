package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorRef, Address, Terminated }
import akka.remote.RemoteClientShutdown
import sabre.algorithm._
import sabre.system.ResultHandler._
import sabre.system.Worker._
import scala.collection.mutable

object Master {
  case class DistributeWork(work: Any)
  case object AllWorkSent

  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)
  case class WorkIsDone(worker: ActorRef)
}

class Master(algorithm: AbstractAlgorithm, resultHandler: ActorRef) extends Actor with ActorLogging {
  import Master._

  val workers = mutable.Map.empty[ActorRef, Option[Any]]
  val workQ = mutable.Queue.empty[Any]

  var allWorkSent = false

  def checkIfAllWorkIsFinished() {
    val noneWorking = workers.foldLeft(true)((b, p) => b && (p._2 == None))
    if (allWorkSent && noneWorking && workQ.isEmpty) resultHandler ! AllResultsSent
  }

  def killAllWorkersAtAddress(addr: Address) {
    def hasAddress(worker: ActorRef): Boolean = worker.path.address == addr

    workers.filter(p => hasAddress(p._1)).foreach { workerWorkPair =>
      val worker = workerWorkPair._1
      if (workers.contains(worker) && workers(worker) != None) {
        log.error("Worker {} died while processing {}.", worker, workers(worker))
        val work = workers(worker).get
        self.tell(DistributeWork(work), self)
      }
      workers -= worker
    }
  }

  def notifyWorkers() {
    if (!workQ.isEmpty) {
      workers.foreach {
        case (worker, m) if (m.isEmpty) => worker ! WorkIsReady
        case _ =>
      }
    }
  }

  override def receive = {
    case WorkerCreated(worker) =>
      log.info("Worker created: {}", worker)
      context.watch(worker)
      workers += (worker -> None)
      worker ! DoAlgorithm(algorithm)
      notifyWorkers()

    case WorkerRequestsWork(worker) =>
      log.info("Worker requests work: {}", worker)
      if (workers.contains(worker)) {
        if (workQ.isEmpty)
          worker ! NoWorkToBeDone
        else if (workers(worker) == None) {
          val work = workQ.dequeue()
          workers += (worker -> Some(work))
          worker.tell(WorkToBeDone(work), resultHandler)
        }
      }

    case WorkIsDone(worker) =>
      if (!workers.contains(worker)) {
        log.error("Unregistered worker {} tried to return finished work!", worker)
      } else {
        // log.info("Worker {} finished work.", worker)
        workers += (worker -> None)
        checkIfAllWorkIsFinished()
      }

    case Terminated(worker) =>
      if (workers.contains(worker) && workers(worker) != None) {
        log.error("Worker {} died while processing {}.", worker, workers(worker))
        val work = workers(worker).get
        self.tell(DistributeWork(work), self)
      }
      workers -= worker

    case RemoteClientShutdown(_, addr) => killAllWorkersAtAddress(addr)

    case DistributeWork(work) =>
      // log.info("Queueing {}", work)
      workQ.enqueue(work)
      notifyWorkers()

    case AllWorkSent => allWorkSent = true

    case badMessage => log.error("Bad message received: {}", badMessage)
  }
}
