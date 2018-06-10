package communication_tests

import actors.TweetAnalyzer._
import actors.analyzingactors.OccurrencesCounter
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._


class TestKitOccurrencesCounter() extends TestKit(ActorSystem("OccurrenceWorld")) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "OccurrencesCounter actor" should {

    "handle Tick message" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("Yo"))
      within(50.millis) {
        occurrencesCounter ! Tick
        expectMsg(Tack)
      }
    }
  }

  "OccurrencesCounter actor" must {

    "Introduce himself " in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("Yo"))
      occurrencesCounter ! IntroduceYourself
      expectMsg(_:String)

    }
  }

  "OccurrencesCounter actor" must {

    "Return results " in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("Yo"))

      occurrencesCounter ! ReturnAnalysisResults
        expectMsg(_:Map[String,AnyVal])
    }
  }

  "OccurrencesCounter actor" must {

    "Handle asynchronous Analysis method" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("yo"))
      occurrencesCounter ! AnalyzeTweet(null)
      expectNoMsg(200.millis)
    }
  }
}
