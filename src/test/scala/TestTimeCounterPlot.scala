
import actors.Customer.Perform
import actors.StreamingClient.DistributeTweetToAnalyze
import actors.{Customer, StreamingClient}
import actors.TweetAnalyzer._
import actors.analyzingactors.{TimeCounter, TweetLangSeparator}
import actors.chartactors.TimeCounterPlot.StartPlot
import actors.chartactors.{LangPlot, TimeCounterPlot}
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._


class TestTimeCounterPlot() extends TestKit(ActorSystem("TimeCounterPlotting")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "TimeCounterPlot actor" should {

    "Return no message" in {
      val timeCounter = system.actorOf(TimeCounterPlot.props(1))
      timeCounter ! ReturnAnalysisResults
      expectNoMsg(1.seconds)
    }
  }


  "TimeCounterPlot actor" should  {

    "Start plot" in {
      val timeCounterPlot = system.actorOf(TimeCounterPlot.props(1))
      timeCounterPlot ! StartPlot
      expectNoMsg(1.seconds)
    }
  }

  "TimeCounterPlot actor" should  {

    "Receive a message" in {
      val streamingClient: ActorRef = system.actorOf(StreamingClient.props(), "streamingActor")
      val timeCounterPlot = system.actorOf(TimeCounterPlot.props(1))
      val analyzerClient: ActorRef = system.actorOf(TimeCounter.props(1), "analyzerActor")
      val client: ActorRef = system.actorOf(Customer.props(1,analyzerClient,timeCounterPlot), "clientActor")
      streamingClient ! DistributeTweetToAnalyze("test",analyzerClient)
      client ! Perform
      expectMsg("test")
    }
  }
}