import actors.analyzingactors.{OccurrencesCounter, TimeCounter, TweetLangSeparator}
import actors.Customer.Perform
import actors.{Customer, StreamingClient}
import akka.actor.{ActorRef, ActorSystem}
import actors.StreamingClient._
import gui.GUI

import scala.io.StdIn._

object Main{

  def main(args: Array[String]) {
    val gui = new GUI
  }
}

