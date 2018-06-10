
import scala.concurrent.duration._
import actors.TweetAnalyzer._
import actors.analyzingactors.TimeCounter
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class TestKitTimeCounter () extends TestKit(ActorSystem("TimeCounterWorld")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "TimeCounter actor" must {

    "Handle asynchronous Analysis method" in {
      val timeCounter = system.actorOf(TimeCounter.props(1))
      timeCounter ! AnalyzeTweet(null)
      expectNoMsg(200.millis)
    }
  }

  "TimeCounter actor" must {

    "Introduce himself " in {
      val timeCounter  = system.actorOf(TimeCounter.props(1))
      timeCounter  ! IntroduceYourself
      receiveN(1)

    }
  }

  "TimeCounter actor" must {

    "Return results " in {
      val timeCounter = system.actorOf(TimeCounter.props(1))

      timeCounter  ! ReturnAnalysisResults
      receiveN(1)
    }
  }

  "TimeCounter actor" should {

    "handle Tick message" in {
      val timeCounter  = system.actorOf(TimeCounter.props(1))
      within(50.millis) {
        timeCounter  ! Tick
        expectMsg(Tack)
      }
    }
  }
}