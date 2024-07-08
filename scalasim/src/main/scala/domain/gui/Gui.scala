package domain.gui

import domain.automaton.Cell
import domain.Environment.{GenericEnvironment, SimpleEnvironment}
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine.Engine.{EngineView, GUIEngine2D}
import domain.engine.GUIEngine2D
import domain.simulations.briansbrain.BriansBrainEnvironment
import domain.simulations.gameoflife.GameOfLifeEnvironment

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JComboBox, JFrame, JPanel, JSlider, WindowConstants}
import javax.swing.{JButton, JFrame, JPanel}
import java.awt.{Color, Graphics}
import javax.swing.{JButton, JComboBox, JFrame, JPanel}
import scala.collection.immutable.LazyList
import domain.simulations.wator.WaTorEnvironment
import domain.simulations.langtonsant.LangtonsAntEnvironment

case class EnvironmentOption[D <: Dimension, R](name: String, createEnvironment: Int => GenericEnvironment[D,R], colors: Map[State, Color])

object EnvironmentOption:
  val options = List(
    EnvironmentOption("Brian's Brain", BriansBrainEnvironment.apply, BriansBrainEnvironment.colors),
    EnvironmentOption("Game of Life", GameOfLifeEnvironment.apply(_, 30), GameOfLifeEnvironment.colors),
    EnvironmentOption("Wa Tor", WaTorEnvironment.apply(_, 10), WaTorEnvironment.colors),
    EnvironmentOption("Langton's Ant", LangtonsAntEnvironment.apply, LangtonsAntEnvironment.colors)
  )

class Gui(val dimension: Tuple2[Int, Int], colors: Map[State, Color]) extends JPanel with EngineView[TwoDimensionalSpace]:
  private var pixels: LazyList[Cell[TwoDimensionalSpace]] = LazyList()
  private var h = dimension(0)
  private var w = dimension(1)
  private var ps = 5

  def setPixelSize(newSize: Int): Unit =
    ps = newSize

  override def updateView(cells: Iterable[Cell[TwoDimensionalSpace]]) =
    pixels = LazyList(cells.toSeq*)
    repaint()

  override def paintComponent(g: Graphics) =
    super.paintComponent(g)
    pixels.foreach(cell =>
      val color = colors.getOrElse(cell.state, Color.WHITE)
      val pos = cell.position
      g.setColor(color)
      g.fillRect(pos.coordinates.head * ps, pos.coordinates.last * ps, ps, ps)
    )

@main def main(): Unit =
  val frame = JFrame("Real Time Pixel Display")
  val startButton = JButton("Start")
  val stopButton = JButton("Stop")
  val exitButton = JButton("Exit")
  val comboBox = JComboBox(EnvironmentOption.options.map(_.name).toArray)
  val sizeSlider = JSlider(10, 200, 100)

  frame.setLayout(null)
  comboBox.setBounds(50, 50, 200, 30)
  sizeSlider.setBounds(50, 150, 200, 50)
  startButton.setBounds(50, 100, 100, 30)
  stopButton.setBounds(150, 100, 100, 30)
  exitButton.setBounds(50, 300, 100, 30)


  frame.add(comboBox)
  frame.add(sizeSlider)
  frame.add(startButton)
  frame.add(stopButton)
  frame.add(exitButton)

  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(3)
  frame.setVisible(true)

  var guiE: Option[GUIEngine2D] = None
  var currentPanel: Option[Gui] = None

  startButton.addActionListener(e =>
    guiE.foreach(guiEngine => guiEngine.stopEngine)
    currentPanel.foreach(panel => frame.remove(panel))

    val selectedOption = comboBox.getSelectedItem.toString
    val size = sizeSlider.getValue()

    val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get
    val env = environmentOption.createEnvironment(size)
    val colors = environmentOption.colors

    val pixelPanel = Gui((size, size), colors)
    val panelSize = 500
    val pixelSize = panelSize / size
    pixelPanel.setPixelSize(pixelSize)

    guiE = Some(GUIEngine2D(env, pixelPanel))
    currentPanel = Some(pixelPanel)

    pixelPanel.setBounds(300, 50, 500, 500)
    frame.add(pixelPanel)
    frame.revalidate()
    frame.repaint()
    guiE.foreach(engine => engine.startEngine)
  )

  stopButton.addActionListener(e =>
    guiE.foreach(guiEngine => guiEngine.stopEngine)
    currentPanel.foreach(panel => frame.remove(panel))
    currentPanel = None
    frame.revalidate()
    frame.repaint()
  )
  exitButton.addActionListener(e =>
    guiE.foreach(guiEngine => guiEngine.stopEngine)
    currentPanel.foreach(panel => frame.remove(panel))
    frame.dispose())