import actors.Customer.Perform
import actors.{Customer, StreamingClient, TimeCounter}
import akka.actor.{ActorRef, ActorSystem}
import actors.StreamingClient._


object Main{

  def main(args: Array[String]) {

    // Create the 'helloAkka' actor system
    val system: ActorSystem = ActorSystem("TwitterStreamingSystem")

    //#create-actors
    // Create the StreamingClient actor
    val streamingClient: ActorRef = system.actorOf(StreamingClient.props(), "streamingActor")

    val analyzerClient: ActorRef = system.actorOf(TimeCounter.props(1), "analyzerActor")

    val client: ActorRef = system.actorOf(Customer.props(analyzerClient), "clientActor")

    //#main-send-messages
    streamingClient ! DistributeTweetToAnalyze("Trump",analyzerClient)
    client ! Perform

  }
}

