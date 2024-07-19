package domain.scalaFxGui
import Monads.*
import Monad.*
import States.*
import State.*
import domain.Environment.GenericEnvironment
import domain.automaton.CellularAutomaton.State as BasicState
import domain.automaton.Cell
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine.Engine.{EngineView, GUIEngine2D}
import domain.engine.{Engine2D, GUIEngine2D}
import domain.scalaFxGui.EnvironmentOption
import domain.scalaFxGui.WindowStateImpl.Window
import domain.simulations.WaTorCellularAutomaton.WatorState.{Fish, Shark}
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.simulations.gameoflife.GameOfLife.CellState as GameOfLifeState
import domain.simulations.gameoflife.GameOfLifeEnvironment
import cats.effect.IO
import domain.exporter.{Exporter, JCodecVideoGenerator, SimpleMatrixToImageConverter}

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JComboBox, JFrame, JLabel, JOptionPane, JPanel, JSlider, JTextField}
import java.util.function.Supplier

trait WindowState:
  type Window
  def initialWindow: Window
  def frame: State[Window, JFrame]
  def setSize(width: Int, height: Int): State[Window, Unit]
  def addButton(text: String, name: String): State[Window, Unit]
  def addLabel(text: String): State[Window, Unit]
  def addComboBox(items: List[EnvironmentOption[? <: Dimension, ?]], name: String): State[Window, Unit]
  def addAutomaton(name: String): State[Window, Unit]
  def addPixelPanel(name: String, panel: String): State[Window, Unit]
  def addInput(name: String): State[Window, Unit]
  def getInputText(name: String): State[Window, String]
  def getSelectedComboBoxItem(name: String): State[Window, EnvironmentOption[? <: Dimension, ?]]
  def clearSouthPanel(): State[Window, Unit]
  def clearPanel(): State[Window, Unit]
  def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], automaton: String, colors: Map[BasicState, Color]): State[Window, Unit]
  def stopEngine(): State[Window, Unit]
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


  def frame: State[Window, JFrame] =
    State(w => (w, w.frame()))
  def initialWindow: Window = createFrame()
  def setSize(width: Int, height: Int): State[Window, Unit] =
    State(w => ((w.setSize(width, height)), {}))
  def addButton(text: String, name: String): State[Window, Unit] =
    State(w => ((w.addButton(text, name)), {}))
  def addLabel(text: String): State[Window, Unit] =
    State(w => ((w.addLabel(text)), {}))
  def addComboBox(items: List[EnvironmentOption[? <: Dimension, ?]], name: String): State[Window, Unit] =
    State(w => ((w.addComboBox(items.toArray, name)), {}))
  def addPixelPanel(name: String, panel: String): State[Window, Unit] =
    State(w => ((w.addPixelPanel(name, panel)), {}))
  def addInput(name: String): State[Window, Unit] =
    State(w => ((w.addInput(name)), {}))

  def addAutomaton(name: String): State[Window, Unit] =
    State(w => ((w.addAutomaton(name)), {}))
  def getSelectedComboBoxItem(name: String): State[Window, EnvironmentOption[? <: Dimension, ?]] =
    State(w => (w, w.getSelectedComboBoxItem(name)))

  def clearSouthPanel(): State[Window, Unit] =
    State(w => (w, w.clearSouthPanel()))
  def getInputText(name: String): State[Window, String] =
    State(w => (w, w.getInputText(name)))
  def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], automaton: String, colors: Map[BasicState, Color]): State[Window, Unit] =
    State(w => ((w.startEngine(env, automaton, colors)), {}))
  def stopEngine(): State[Window, Unit] =
    State(w => ((w.stopEngine()), {}))

  def clearPanel(): State[Window, Unit] =
    State(w => (w, w.clearPanel()))
  def toLabel(text: String, name: String): State[Window, Unit] =
    State(w => ((w.showToLabel(text, name)), {}))
  def show(): State[Window, Unit] =
    State(w => (w.show(), {}))

  def showAutomaton(name: String): State[Window, Unit] =
    State(w => (w.showAutomaton(name), {}))
  def exec(cmd: =>Unit): State[Window, Unit] =
    State(w => (w, cmd))
  def eventStream(): State[Window, LazyList[String]] =
    State(w => (w, LazyList.generate(w.events())))
  def dialog(text: String): State[Window, Unit] =
    for
      frame <- frame
      _ <- exec(JOptionPane.showMessageDialog(frame, text))
    yield ()

@main def windowStateExample =
  import WindowStateImpl.*

  val windowCreation = for
    _ <- setSize(1000, 600)
    _ <- addButton(text = "Start", name = "StartButton")
    _ <- addButton(text = "Stop", name = "StopButton")
    _ <- addButton(text = "Exit", name = "ExitButton")
    _ <- addButton(text = "Export Video", name = "ExportButton")
    _ <- addComboBox(EnvironmentOption.options, "AutomatonsComboBox")
    _ <- show()
    e <- eventStream()
  yield e

  val gameOfLife = for
    _ <- addLabel("Width")
    _ <- addInput("Width")
    _ <- addLabel("Height")
    _ <- addInput("Height")
    _ <- addLabel("ALIVE")
    _ <- addInput("Alive")
  yield()

  val waTor = for
    _ <- addLabel("Width")
    _ <- addInput("Width")
    _ <- addLabel("Height")
    _ <- addInput("Height")
    _ <- addLabel("FISH")
    _ <- addInput("Fish")
  yield ()

  val langtonsAnt = for
    _ <- addLabel("Width")
    _ <- addInput("Width")
    _ <- addLabel("Height")
    _ <- addInput("Height")
  yield ()

  val briansBrain = for
    _ <- addLabel("Dimension")
    _ <- addInput("Dimension")
    _ <- addLabel("ON")
    _ <- addInput("On")
  yield ()

  def createEnvironment(option: EnvironmentOption[_, _]): State[Window, GenericEnvironment[TwoDimensionalSpace, _]] = {
    option.name match {
      case "Brian's Brain" =>
        for {
          dimensionStr <- getInputText("Dimension")
          onCellsStr <- getInputText("On")
        } yield {
          val dimension = dimensionStr.toIntOption.getOrElse(200)
          val onCells = onCellsStr.toIntOption.getOrElse(dimension * dimension / 3)
          val initialCells = Map(CellState.ON -> onCells)
          option.createEnvironment(dimension, dimension, initialCells)
        }
      case "Game of Life" =>
        for {
          heightStr <- getInputText("Height")
          widthStr <- getInputText("Width")
          aliveCellsStr <- getInputText("Alive")
        } yield {
          val height = heightStr.toIntOption.getOrElse(200)
          val width = widthStr.toIntOption.getOrElse(200)
          val aliveCells = aliveCellsStr.toIntOption.getOrElse(height * width / 3)
          val initialCells = Map(GameOfLifeState.ALIVE -> aliveCells)
          option.createEnvironment(width, height, initialCells)
        }
      case "Wa Tor" =>
        for {
          heightStr <- getInputText("Height")
          widthStr <- getInputText("Width")
          fishCellsStr <- getInputText("Fish")
        } yield {
          val height = heightStr.toIntOption.getOrElse(200)
          val width = widthStr.toIntOption.getOrElse(200)
          val fishCells = fishCellsStr.toIntOption.getOrElse(height * width / 3)
          val initialCells = Map(Fish() -> fishCells)
          option.createEnvironment(width, height, initialCells)
        }
      case "Langton's Ant" =>
        for {
          heightStr <- getInputText("Height")
          widthStr <- getInputText("Width")
        } yield {
          val height = heightStr.toIntOption.getOrElse(200)
          val width = widthStr.toIntOption.getOrElse(200)
          val initialCells = Map()
          option.createEnvironment(width, height, initialCells)
        }
      case _ => throw new IllegalArgumentException(s"Unknown environment: ${option.name}")
    }
  }

  val windowEventsHandling = for
    _ <- windowCreation
    e <- eventStream()
    _ <- seqN(e.map {
      case "StartButton" =>
        for {
          automatonOpt <- getSelectedComboBoxItem("AutomatonsComboBox")
          _ <- automatonOpt match {
            case option: EnvironmentOption[_, _] =>
              for {
                environment <- createEnvironment(option)
                _ <- stopEngine()
                _ <- startEngine(environment, option.name, option.colors)
              } yield ()
          }
        } yield ()
      case "ExportButton" =>
        for {
          automatonOpt <- getSelectedComboBoxItem("AutomatonsComboBox")
          _ <- automatonOpt match {
            case option: EnvironmentOption[_, _] =>
              for {
                environment <- createEnvironment(option)
                _ <- {

                  val engine = Engine2D(environment, 5)
                  engine.startEngine
                  Thread.sleep(2000)
                  engine.stopEngine

                  Exporter.exportMatrix(
                    engine,
                    option.colors,
                    converter = SimpleMatrixToImageConverter,
                    videoGenerator = JCodecVideoGenerator,
                    cellSize = 10,
                    videoFilename = "output.mp4",
                    secondsPerImage = 0.1
                  )
                  dialog("Video exported as output.mp4")
                }
              } yield ()
          }
        } yield ()
      case "AutomatonsComboBox" =>
        for
          automaton <- getSelectedComboBoxItem("AutomatonsComboBox")
          _ <- clearSouthPanel()
          _ <- clearPanel()
          _ <- stopEngine()
          _ <- automaton match
            case option: EnvironmentOption[_, _] =>
              option.name match
                case "Brian's Brain" => briansBrain
                case "Game of Life" => gameOfLife
                case "Wa Tor" => waTor
                case "Langton's Ant" => langtonsAnt
                case _ => throw new IllegalArgumentException(s"Unknown environment: ${option.name}")
        yield ()
      case "StopButton" =>
        for
          _ <- stopEngine()
        yield()
      case "ExitButton" => exec(sys.exit())
    })
  yield ()

  windowEventsHandling.run(initialWindow)