
import actors.{Customer, StreamingClient}
import actors.Customer.Perform
import actors.StreamingClient.DistributeTweetToAnalyze
import actors.TweetAnalyzer._
import actors.analyzingactors.{OccurrencesCounter, TweetLangSeparator}
import actors.chartactors.TimeCounterPlot.StartPlot
import actors.chartactors.{CounterPlot, LangPlot}
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._


class TestCounterPlot() extends TestKit(ActorSystem("CounterPlotting")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "CounterPlot actor" should  {

    "Return message" in {
      val counterPlot = system.actorOf(CounterPlot.props(1, "scala"))
      counterPlot ! ReturnAnalysisResults
      expectNoMsg(1.seconds)
    }
  }

  "CounterPlot actor" should  {

    "Start plot" in {
      val counterPlot = system.actorOf(CounterPlot.props(1, "scala"))
      counterPlot ! StartPlot
      expectNoMsg(1.seconds)
    }
  }

  "CounterPlot actor" should  {

    "Receive a message" in {
      val streamingClient: ActorRef = system.actorOf(StreamingClient.props(), "streamingActor")
      val counterPlot = system.actorOf(CounterPlot.props(1, "test"))
      val analyzerClient: ActorRef = system.actorOf(OccurrencesCounter.props("test"), "analyzerActor")
      val client: ActorRef = system.actorOf(Customer.props(1,analyzerClient,counterPlot), "clientActor")
      streamingClient ! DistributeTweetToAnalyze("test",analyzerClient)
      client ! Perform
      expectMsg("test")
    }
  }
}