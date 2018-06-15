
import actors.{Customer, StreamingClient}
import actors.Customer.Perform
import actors.StreamingClient.DistributeTweetToAnalyze
import actors.TweetAnalyzer._
import actors.analyzingactors.{OccurrencesCounter, TimeCounter, TweetLangSeparator}
import actors.chartactors.TimeCounterPlot.StartPlot
import actors.chartactors.{CounterPlot, LangPlot}
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._


class TestLangPlot() extends TestKit(ActorSystem("LangPlotting")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "LangPlot actor" should {

    "Return no message" in {
      val langPlot = system.actorOf(LangPlot.props(1, scala.collection.mutable.Map("test" -> true)))
      langPlot ! ReturnAnalysisResults
      expectNoMsg(1.seconds)
    }
  }

  "LangPlot actor" should  {

    "Start plot" in {
      val langPlot = system.actorOf(LangPlot.props(1,scala.collection.mutable.Map("test" -> true)))

      langPlot ! StartPlot
      expectNoMsg(1.seconds)
    }
  }
  "LangPlot actor" should  {

    "Receive a message" in {
      val streamingClient: ActorRef = system.actorOf(StreamingClient.props(), "streamingActor")
      val langPlot = system.actorOf(LangPlot.props(1,scala.collection.mutable.Map("test" -> true)))
      val analyzerClient: ActorRef = system.actorOf(TweetLangSeparator.props(1), "analyzerActor")
      val client: ActorRef = system.actorOf(Customer.props(1,analyzerClient,langPlot), "clientActor")
      streamingClient ! DistributeTweetToAnalyze("test",analyzerClient)
      client ! Perform
      expectMsg("test")
    }
  }


}