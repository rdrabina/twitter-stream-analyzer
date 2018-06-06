package actors

import actors.TweetAnalyzer.ReturnAnalysisResults
import akka.actor.{Actor, ActorRef, Props}

object Customer{

  def props(actor:ActorRef): Props = Props(new Customer(actor))


  final case class MessageMap(message: Map[Any,Any])
  case object  Perform
}

class Customer(val actor:ActorRef) extends Actor{
  import Customer._


  def receive: PartialFunction[Any, Unit] = {

    case Perform =>
        actor ! ReturnAnalysisResults

    case message  =>
      println("")
      println("==== "+message.toString+" ====")
      println("")
      Thread.sleep(1000)
      actor ! ReturnAnalysisResults
  }

}
