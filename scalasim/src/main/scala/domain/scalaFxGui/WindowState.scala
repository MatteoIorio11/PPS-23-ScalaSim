package domain.scalaFxGui
import Monads.*
import Monad.*
import States.*
import State.*
import domain.engine.Engine.GUIEngine2D
import domain.engine.GUIEngine2D
import domain.gui.EnvironmentOption

import java.util.function.Supplier
import javax.swing.JOptionPane

trait WindowState:
  type Window
  def initialWindow: Window
  def setSize(width: Int, height: Int): State[Window, Unit]
  def addButton(text: String, name: String): State[Window, Unit]
  def addLabel(text: String, name: String): State[Window, Unit]
  def addComboBox(items: Seq[String], name: String): State[Window, Unit]
  def addAutomaton(name: String): State[Window, Unit]
  def addPixelPanel(name: String, panel: String): State[Window, Unit]
  def addInput(name: String, panel: String): State[Window, Unit]
  def getSelectedComboBoxItem(name: String): State[Window, String]
  def toLabel(text: String, name: String): State[Window, Unit]
  def show(): State[Window, Unit]
  def showAutomaton(name: String): State[Window, Unit]
  def exec(cmd: =>Unit): State[Window, Unit]
  def eventStream(): State[Window, LazyList[String]]

extension (so: LazyList.type)
    def generate[A](source: Supplier[A]): LazyList[A] = LazyList.iterate[A](source.get())(_ => source.get())
object WindowStateImpl extends WindowState:
  import SwingFunctionalFacade.*

  type Window = Frame


  def initialWindow: Window = createFrame

  def setSize(width: Int, height: Int): State[Window, Unit] =
    State(w => ((w.setSize(width, height)), {}))
  def addButton(text: String, name: String): State[Window, Unit] =
    State(w => ((w.addButton(text, name)), {}))
  def addLabel(text: String, name: String): State[Window, Unit] =
    State(w => ((w.addLabel(text, name)), {}))
  def addComboBox(items: Seq[String], name: String): State[Window, Unit] =
    State(w => ((w.addComboBox(items.toArray, name)), {}))
  def addPixelPanel(name: String, panel: String): State[Window, Unit] =
    State(w => ((w.addPixelPanel(name, panel)), {}))
  def addInput(name: String, panel: String): State[Window, Unit] =
    State(w => ((w.addInput(name, panel)), {}))

  def addAutomaton(name: String): State[Window, Unit] =
    State(w => ((w.addAutomaton(name)), {}))
  def getSelectedComboBoxItem(name: String): State[Window, String] =
    State(w => (w, w.getSelectedComboBoxItem(name)))
  def toLabel(text: String, name: String): State[Window, Unit] =
    State(w => ((w.showToLabel(text, name)), {}))
  def show(): State[Window, Unit] =
    State(w => (w.show, {}))

  def showAutomaton(name: String): State[Window, Unit] =
    State(w => (w.showAutomaton(name), {}))
  def exec(cmd: =>Unit): State[Window, Unit] =
    State(w => (w, cmd))
  def eventStream(): State[Window, LazyList[String]] =
    State(w => (w, LazyList.generate(w.events)))

@main def windowStateExample =
  import WindowStateImpl.*

  var guiE: Option[GUIEngine2D] = None
  val windowCreation = for
    _ <- setSize(1000, 600)
    _ <- addButton(text = "Start", name = "StartButton")
    _ <- addButton(text = "Stop", name = "StopButton")
    _ <- addButton(text = "Exit", name = "ExitButton")
    _ <- addButton(text = "Export Video", name = "ExportButton")
    _ <- addComboBox(Seq("Option 1", "Option 2", "Option 3"), "AutomatonsComboBox")
    _ <- show()
    e <- eventStream()
  yield e

  val gameOfLifeAutomaton = for
    _ <- addAutomaton("Game-Of-Life")
    _ <- addPixelPanel("Game-Of-Life-Panel", "Game-Of-Life")
    _ <- addInput("Game-Of-Life-Width", "Game-Of-Life-Panel")
    _ <- addInput("Game-Of-Life-Height", "Game-Of-Life-Panel")
  yield e


  val windowEventsHandling = for
    _ <- windowCreation
    e <- eventStream()
    _ <- seqN(e.map {
      case "StartButton" =>
        for
          automaton <- getSelectedComboBoxItem("AutomatonsComboBox")
          _ <-
            automaton match
              case Some(c) =>
                for
                  _ <- cleanPanel()
                  _ <- showAutomaton(automaton)
                yield ()
              case None =>
                dialog("Unknown Automaton")
        yield ()
      case "" => ???
      case "StopButton" => toLabel("Stop button clicked", "Label1")
      case "ExitButton" => exec(sys.exit())
    })
  yield ()

  windowEventsHandling.run(initialWindow)