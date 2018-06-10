

import java.util.Calendar
import actors.TweetAnalyzer.{AnalyzeTweet, ReturnAnalysisResults, Tick}
import actors.analyzingactors.OccurrencesCounter
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.danielasfregola.twitter4s.entities.Tweet
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class TestOccurrenceAnalysis() extends TestKit(ActorSystem("OxWorld")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "OccurrencesCounter actor" must {

    "Return map with value 0 without any tick " in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("Yo"))
      occurrencesCounter ! ReturnAnalysisResults
      expectMsg(Map("Yo" -> 0))

    }
  }

  "OccurrencesCounter actor" should {

    "Return actual results" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      within(50.millis) {
        occurrencesCounter ! ReturnAnalysisResults
        expectMsg(Map("TestKeyword" -> 0))
      }
    }
  }


  "OccurrencesCounter actor" must {

    "Increment counter after receiving analyze tweet request" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("Yo"))
      occurrencesCounter ! AnalyzeTweet(null)
      expectNoMsg(1.second)

    }
  }

  "OccurrencesCounter actor" should {

    "Handle analyze tweet request" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      within(50.millis) {
        occurrencesCounter ! AnalyzeTweet(null)
        expectNoMsg(1.second)
      }
    }
  }

  "OccurrencesCounter actor" must {

    "Return map with state one after incrementation" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")
      occurrencesCounter ! AnalyzeTweet(tweet)
      occurrencesCounter ! ReturnAnalysisResults
      expectMsg(Map("TestKeyword" -> 1))

    }
  }

  "OccurrencesCounter actor" must {

    "Return map with state 5 after 5 analysis request" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")
      for (_ <- 1 to 5) occurrencesCounter ! AnalyzeTweet(tweet)
      occurrencesCounter ! ReturnAnalysisResults
      expectMsg(Map("TestKeyword" -> 5))

    }
  }

  "OccurrencesCounter actor" must {

    "Return map with state 3 after 3 analysis request and cannot change state after  Tick" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")
      for (_ <- 1 to 3) occurrencesCounter ! AnalyzeTweet(tweet)
      occurrencesCounter ! Tick
      occurrencesCounter ! Tick
      occurrencesCounter ! ReturnAnalysisResults
      receiveN(2)
      expectMsg(Map("TestKeyword" -> 3))

    }
  }

  "OccurrencesCounter actor" should {

    "Handle 10 analyze tweet request" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      within(400.millis) {
        for (_ <- 1 to 10) occurrencesCounter ! AnalyzeTweet(tweet)
        expectNoMsg(1.second)
      }
    }
  }

  "OccurrencesCounter actor" should {

    "Handle 10 answer request" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")
      for (_ <- 1 to 10) occurrencesCounter ! AnalyzeTweet(tweet)
      within(400.millis) {
        for (_ <- 1 to 10)  occurrencesCounter ! ReturnAnalysisResults
        receiveN(9)
        expectMsg(Map("TestKeyword" -> 10))
      }
    }
  }

  "OccurrencesCounter actor" should {

    "Handle 1000 analysis request" in {
      val occurrencesCounter = system.actorOf(OccurrencesCounter.props("TestKeyword"))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      within(1.second) {
        for (_ <- 1 to 10000) occurrencesCounter ! AnalyzeTweet(tweet)
        occurrencesCounter ! ReturnAnalysisResults
        expectMsg(Map("TestKeyword" -> 10000))
      }
    }
  }

}
