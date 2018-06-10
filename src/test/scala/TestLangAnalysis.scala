
import java.util.Calendar

import actors.TweetAnalyzer.{AnalyzeTweet, ReturnAnalysisResults}
import actors.analyzingactors.TweetLangSeparator
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.danielasfregola.twitter4s.entities.Tweet
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._


class TestLangAnalysis() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "TweetLangSeparator actor" must {

    "Return map with value 0 after result request" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))
      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map())

    }
  }

  "TweetLangSeparator actor" must {

    "Return 1 after one analyse request" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(0))
      val tweet:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))

      tweetLangSeparator ! AnalyzeTweet(tweet)
      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en"->1))
    }
  }

  "TweetLangSeparator actor" must {

    "Handle undef lang and ignore it" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(0))
      val tweet:Tweet = Tweet(created_at=Calendar.getInstance().getTime,id=12, id_str="a",source="b",text="b")

      tweetLangSeparator ! AnalyzeTweet(tweet)
      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map())
    }
  }

  "TweetLangSeparator actor" must {

    "Create output for more tha one lang" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(0))
      val tweet1:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))
      val tweet2:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("de"))

      tweetLangSeparator ! AnalyzeTweet(tweet1)
      tweetLangSeparator ! AnalyzeTweet(tweet2)
      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en"->1 , "de"->1))
    }
  }

  "TweetLangSeparator actor" should {

    "Handle 100000 analysis request" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(0))
      val tweet:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))
      within(3.second) {
        for (_ <- 1 to 10000) tweetLangSeparator ! AnalyzeTweet(tweet)
        Thread.sleep(1000)
        tweetLangSeparator ! ReturnAnalysisResults
        expectMsg(Map("en" -> 10000))
      }
    }
  }

  "TweetLangSeparator actor" must {

    "Must act like timerCounter when timer_sec is greater than 0 part 1" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))
      val tweet:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))

      tweetLangSeparator ! AnalyzeTweet(tweet)
      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en" -> 0))
    }
  }

  "TweetLangSeparator actor" must {

    "Must act like timerCounter when timer_sec is greater than 0 part 2" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))
      val tweet:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))

      tweetLangSeparator ! AnalyzeTweet(tweet)
      Thread.sleep(1500)
      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en" -> 1))
    }
  }

  "TweetLangSeparator actor" should {

    "Return the same answers as requests" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(0))
      val tweet:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))
      for (_ <- 1 to 100000) tweetLangSeparator ! AnalyzeTweet(tweet)

      within(2.second) {
        for (_ <- 1 to 100000) tweetLangSeparator ! ReturnAnalysisResults
        receiveN(100000)
      }
    }
  }

  "TweetLangSeparator actor" must {

    "Reset when it works in timer mode" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))
      val tweet:Tweet =
        Tweet(
          created_at=Calendar.getInstance().getTime,
          id=12,
          id_str="a",
          source="b",
          text="b",
          lang = Some("en"))

      tweetLangSeparator ! AnalyzeTweet(tweet)

      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en" -> 0))
      Thread.sleep(1200)

      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en" -> 1))
      Thread.sleep(2200)

      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(Map("en" -> 0))
    }
  }

//  "TweetLangSeparator actor" should {
//
//    "Return actual results" in {
//      val occurrencesCounter = system.actorOf(TweetLangSeparator.props(1))
//      within(50.millis) {
//        occurrencesCounter ! ReturnAnalysisResults
//        expectMsg(Map())
//      }
//    }
//  }


}
