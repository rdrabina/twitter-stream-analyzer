
import actors.TweetAnalyzer.{IntroduceYourself, ReturnAnalysisResults, Tack, Tick}
import actors.analyzingactors.TweetLangSeparator
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._


class TestKitTweetLangSeparator () extends TestKit(ActorSystem("LangWorld")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "TweetLangSeparator actor" must {

    "Introduce himself " in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))
      tweetLangSeparator ! IntroduceYourself
      receiveN(1)

    }
  }

  "TweetLangSeparator actor" should {

    "handle Tick message" in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))
      within(50.millis) {
        tweetLangSeparator ! Tick
        expectMsg(Tack)
      }
    }
  }

  "TweetLangSeparator actor" must {

    "Return results " in {
      val tweetLangSeparator = system.actorOf(TweetLangSeparator.props(1))

      tweetLangSeparator ! ReturnAnalysisResults
      expectMsg(_:Map[String,AnyVal])
    }
  }

}
