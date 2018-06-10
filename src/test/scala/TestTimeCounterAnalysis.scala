
import java.util.Calendar

import actors.TweetAnalyzer.{AnalyzeTweet, ReturnAnalysisResults, Tick}
import actors.analyzingactors.TimeCounter
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.danielasfregola.twitter4s.entities.Tweet
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class TestTimeCounterAnalysis() extends TestKit(ActorSystem("TimeCounterSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "TimeCounter actor" must {

    "Return map with value 0 after result request" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 0))

    }
  }

  "TimeCounter actor" must {

    "Return map with value 10 after 10 result request" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")
      for (_ <- 1 to 10000) timeCounter ! AnalyzeTweet(tweet)
      Thread.sleep(1500)
      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 10000))

    }
  }

  "TimeCounter actor" should {

    "Handle 100000 analysis request" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      within(2.second) {
        for (_ <- 1 to 100000) timeCounter ! AnalyzeTweet(tweet)
        Thread.sleep(1500)
        timeCounter ! ReturnAnalysisResults
        expectMsg(Map("result" -> 100000))
      }
    }
  }

  "TimeCounter actor" must {

    "Return map with value 0 when request is before sync auto-tic" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")
      for (_ <- 1 to 100) timeCounter ! AnalyzeTweet(tweet)
      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 0))
    }
  }

  "TimeCounter actor" must {

    "Clean state after tick signal" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      for (_ <- 1 to 100) timeCounter ! AnalyzeTweet(tweet)
      Thread.sleep(1500)
      timeCounter ! ReturnAnalysisResults

      expectMsg(Map("result" -> 100))

      timeCounter ! Tick
      receiveN(1)

      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 0))
    }
  }

  "TimeCounter actor" should {

    "Give as many answers as there was requests" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      within(2.second) {
        for (_ <- 1 to 100000) timeCounter ! AnalyzeTweet(tweet)
        for (_ <- 1 to 10000) timeCounter ! ReturnAnalysisResults
        receiveN(10000)
      }
    }
  }

  "TimeCounter actor" should {

    "Handle 10000000 incrementation fast" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      within(3.second) {
        for (_ <- 1 to 10000000) timeCounter ! AnalyzeTweet(tweet)
        expectNoMsg(1.second)
      }
    }
  }

  "TimeCounter actor" must {

    "Not clean state before timer " in {
      val timeCounter = system.actorOf(TimeCounter.props(5))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      for (_ <- 1 to 1000) timeCounter ! AnalyzeTweet(tweet)

      Thread.sleep(3000)
      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 1000))
    }
  }

  "TimeCounter actor" must {

    "Must clean state after receiving tick" in {
      val timeCounter = system.actorOf(TimeCounter.props(5))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      for (_ <- 1 to 1000) timeCounter ! AnalyzeTweet(tweet)

      Thread.sleep(2000)
      timeCounter ! Tick
      receiveN(1)

      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 0))
    }
  }

  "TimeCounter actor" must {

    "Start from begin after timer" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      for (_ <- 1 to 1000) timeCounter ! AnalyzeTweet(tweet)

      Thread.sleep(2000)

      for (_ <- 1 to 500) timeCounter ! AnalyzeTweet(tweet)

      timeCounter ! Tick
      receiveN(1)

      timeCounter ! ReturnAnalysisResults
      expectMsg(Map("result" -> 500))
    }
  }

}
