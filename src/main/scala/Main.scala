import actors.analyzingactors.{OccurrencesCounter, TimeCounter, TweetLangSeparator}
import actors.Customer.Perform
import actors.{Customer, StreamingClient}
import akka.actor.{ActorRef, ActorSystem}
import actors.StreamingClient._
import scala.io.StdIn._

object Main{

  def main(args: Array[String]) {

    println("Wybierz tryb:")
    val mode:String = readLine()
    println("Podaj keyword:")
    val keyword:String = readLine()

    var timer:Int =0
    if(mode == "Lang" || mode == "TimeCounter" ) {
      println("Podaj rozmiar ramki czasowej:")
      timer = readInt()
    }


    // Create the 'helloAkka' actor system
    val system: ActorSystem = ActorSystem("TwitterStreamingSystem")

    //#create-actors
    // Create the StreamingClient actor
    val streamingClient: ActorRef = system.actorOf(StreamingClient.props(), "streamingActor")

    val analyzerClient: ActorRef =
      mode match {
        case "Lang" =>
          system.actorOf(TweetLangSeparator.props(timer), "analyzerActor")
        case "Counter" =>
          system.actorOf(OccurrencesCounter.props(keyword), "analyzerActor")
        case "TimeCounter" =>
          system.actorOf(TimeCounter.props(timer), "analyzerActor")
        case _ =>
          return
    }

    val client: ActorRef = system.actorOf(Customer.props(analyzerClient), "clientActor")

    //#main-send-messages
    streamingClient ! DistributeTweetToAnalyze(keyword,analyzerClient)
    client ! Perform

  }
}

