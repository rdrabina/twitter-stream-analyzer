
import actors.StreamingClient
import actors.StreamingClient.{DistributeTweetToAnalyze, PrintSampleStream}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._

class TestKitStreamingClient() extends TestKit(ActorSystem("MyTests")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

//  "StreamingClient actor 1" must {
//
//    "return Ok message after receiving PrintSampleStream" in {
//      val streamer = system.actorOf(StreamingClient.props())
//      streamer ! PrintSampleStream
//      expectMsg("Ok")
//    }
//
//  }
//
//  "StreamingClient actor 2" must {
//
//    "return Ok message after receiving PrintSampleStream" in {
//      val streamer = system.actorOf(StreamingClient.props())
//      streamer ! PrintSampleStream
//      expectMsg("Ok")
//    }
//
//  }
//  "StreamingClient actor 3" must {
//
//    "return keyword message after receivingDistributeTweetToAnalyze(keyword,analyzers)" in {
//      val streamer = system.actorOf(StreamingClient.props())
//      streamer ! DistributeTweetToAnalyze("Test",null)
//      expectMsg("Test")
//    }
//
//  }
//
//  "StreamingClient Time Testing" should {
//
//    "Start Stream" in {
//      val streamer = system.actorOf(StreamingClient.props())
//      within(500.millis) {
//        streamer ! PrintSampleStream
//        expectMsg("Ok")
//      }
//    }
//  }


}
