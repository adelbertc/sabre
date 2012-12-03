package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }
import sabre.algorithm._
import sabre.system.SabreMasterProtocol._
import sabre.system.MasterWorkerProtocol._
import sabre.system.MasterResultHandlerProtocol._
import scala.collection.mutable

class Master(algorithm: AbstractAlgorithm, resultHandler: ActorRef) extends Actor with ActorLogging {
  val workers = mutable.Map.empty[ActorRef, Option[Any]]
  val workQ = mutable.Queue.empty[Any]

  var allWorkSent = false

  def checkIfAllWorkIsFinished(): Unit = {
    val noneWorking = workers.foldLeft(true)((b, p) => b && (p._2 == None))
    if (allWorkSent && noneWorking && workQ.isEmpty) resultHandler ! AllResultsSent
  }

  def notifyWorkers(): Unit = {
    if (!workQ.isEmpty) {
      workers.foreach {
        case (worker, m) if (m.isEmpty) => worker ! WorkIsReady
        case _ =>
      }
    }
  }

  def receive = {
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
        self.tell(work, self)
      }
      workers -= worker

    case DistributeWork(work) =>
      // log.info("Queueing {}", work)
      workQ.enqueue(work)
      notifyWorkers()

    case AllWorkSent => allWorkSent = true

    case badMessage => log.error("Bad message received: {}", badMessage)
  }
}
