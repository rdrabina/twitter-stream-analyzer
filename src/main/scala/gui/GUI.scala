package gui

import java.awt.{Color, Font, event}
import javax.swing._

import actors.Customer.Perform
import actors.StreamingClient.DistributeTweetToAnalyze
import actors.analyzingactors.{OccurrencesCounter, TimeCounter, TweetLangSeparator}
import actors.chartactors.{CounterPlot, LangPlot, TimeCounterPlot}
import actors.{Customer, StreamingClient}
import akka.actor.{ActorRef, ActorSystem}


class GUI extends JFrame{

  setSize(600, 400)
  setLayout(null)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setTitle("Twitter stream analyzer")

  val titleLabel: JLabel = new JLabel("Choose analysis specifications")
  titleLabel.setForeground(Color.blue)
  titleLabel.setFont(new Font("Serif", Font.BOLD, 20))

  val mode: JLabel = new JLabel("Mode:")
  val keyWord: JLabel = new JLabel("Keyword:")
  val timePeriod: JLabel = new JLabel("Time period: [s]")
  val exception: JLabel = new JLabel("* time period is unnecessary if you choose mode counter.")
  exception.setFont(new Font("Serif", Font.BOLD, 12))

  val modeComboBox: JComboBox[String] = new JComboBox[String](Array("Lang", "TimeCounter", "Counter"))
  val keyWordTextField: JTextField = new JTextField()
  val timePeriodTextField: JTextField = new JTextField()


  val okButton: JButton = new JButton("Ok")
  okButton.addActionListener((e: event.ActionEvent) => {
    val modeString: String = modeComboBox.getSelectedItem.toString
    var timePeriodInt:Int = 1
    val keyWordString: String = keyWordTextField.getText
    try {
      if(modeString != "Counter")timePeriodInt = timePeriodTextField.getText.toInt
      if(keyWordString == "") throw new IllegalArgumentException
      if(timePeriodInt < 1 && !(modeString == "Counter")) throw new IllegalArgumentException
      JOptionPane.showMessageDialog(okButton, "Click ok to show chart")
      setVisible(false)
      dispose()
      createActors(modeString, keyWordString, timePeriodInt)
    }
    catch {
      case numberFormatException:NumberFormatException =>
        JOptionPane.showMessageDialog(okButton, "Wrong time period. Try again")
      case illegalArgumentException:IllegalArgumentException =>
        JOptionPane.showMessageDialog(okButton, "Keyword field cannot be empty. Time period must be more than 0. Try again")
    }

  })
  val clearButton: JButton = new JButton("Clear")
    clearButton.addActionListener((e: event.ActionEvent) =>{
      keyWordTextField.setText("")
      timePeriodTextField.setText("")
    })
  val cancelButton: JButton = new JButton("Cancel")
    cancelButton.addActionListener((e:event.ActionEvent) =>{
      JOptionPane.showMessageDialog(okButton, "Bye bye")
      setVisible(false)
      dispose()
    })

  titleLabel.setBounds(100,30,400,30)
  mode.setBounds(80,70,200,30)
  keyWord.setBounds(80,110,200,30)
  timePeriod.setBounds(80,150,200,30)
  modeComboBox.setBounds(300,70,200,30)
  keyWordTextField.setBounds(300,110,200,30)
  timePeriodTextField.setBounds(300,150,200,30)
  exception.setBounds(80,190,500,30)
  okButton.setBounds(100,250,100,30)
  clearButton.setBounds(250,250,100,30)
  cancelButton.setBounds(400,250,100,30)
  add(titleLabel)
  add(mode)
  add(keyWord)
  add(timePeriod)
  add(modeComboBox)
  add(keyWordTextField)
  add(timePeriodTextField)
  add(exception)
  add(okButton)
  add(clearButton)
  add(cancelButton)
  setVisible(true)

  def createActors(mode:String, keyWord: String, timePeriod: Int): Unit ={
    // Create the 'helloAkka' actor system
    val system: ActorSystem = ActorSystem("TwitterStreamingSystem")

    //#create-actors
    // Create the StreamingClient actor
    val streamingClient: ActorRef = system.actorOf(StreamingClient.props(), "streamingActor")


      mode match {
        case "Lang" =>
          val langPlot: ActorRef = system.actorOf(LangPlot.props(timePeriod), "langPlotActor")
          val analyzerClient:ActorRef = system.actorOf(TweetLangSeparator.props(timePeriod), "analyzerActor")
          val client: ActorRef = system.actorOf(Customer.props(timePeriod,analyzerClient,langPlot), "clientActor")
          //#main-send-messages
          streamingClient ! DistributeTweetToAnalyze(keyWord,analyzerClient)
          client ! Perform
        case "Counter" =>
          val counterPlot: ActorRef = system.actorOf(CounterPlot.props(timePeriod,keyWord), "counterPlotActor")
          val analyzerClient: ActorRef = system.actorOf(OccurrencesCounter.props(keyWord), "analyzerActor")
          val client: ActorRef = system.actorOf(Customer.props(timePeriod,analyzerClient,counterPlot), "clientActor")
          //#main-send-messages
          streamingClient ! DistributeTweetToAnalyze(keyWord,analyzerClient)
          client ! Perform

        case "TimeCounter" =>
          val timeCounterPlot: ActorRef = system.actorOf(TimeCounterPlot.props(timePeriod), "timeCounterPlotActor")
          val analyzerClient: ActorRef = system.actorOf(TimeCounter.props(timePeriod), "analyzerActor")
          val client: ActorRef = system.actorOf(Customer.props(timePeriod,analyzerClient,timeCounterPlot), "clientActor")
          //#main-send-messages
          streamingClient ! DistributeTweetToAnalyze(keyWord,analyzerClient)
          client ! Perform
     }
  }

}
