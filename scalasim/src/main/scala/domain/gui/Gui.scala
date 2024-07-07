package domain.gui

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.TwoDimensionalSpace
import domain.engine.Engine.{EngineView, GUIEngine2D}
import domain.engine.GUIEngine2D
import domain.simulations.briansbrain.BriansBrainEnvironment
import domain.simulations.gameoflife.GameOfLifeEnvironment

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JFrame, JPanel}
import java.awt.{Color, Graphics}
import javax.swing.{JButton, JComboBox, JFrame, JPanel}
import scala.collection.immutable.LazyList
import domain.simulations.wator.WaTorEnvironment

class Gui(val dimension: Tuple2[Int, Int], colors: Map[State, Color]) extends JPanel with EngineView[TwoDimensionalSpace]:
  private var pixels: LazyList[Cell[TwoDimensionalSpace]] = LazyList()
  private var h = dimension(0)
  private var w = dimension(1)
  private var ps = 5
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
  val automatonOptions = Array("Brian's Brain", "Game of Life", "Wa Tor")
  val comboBox = JComboBox(automatonOptions)

  frame.setLayout(null)
  comboBox.setBounds(50, 50, 200, 30)
  startButton.setBounds(50, 100, 100, 30)
  stopButton.setBounds(150, 100, 100, 30)
  exitButton.setBounds(50, 300, 100, 30)


  frame.add(comboBox)
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

    val selected = comboBox.getSelectedItem.toString
    val (env, colors) = selected match
      case "Brian's Brain" => (BriansBrainEnvironment(100), BriansBrainEnvironment.colors)
      case "Game of Life"  => (GameOfLifeEnvironment(100), GameOfLifeEnvironment.colors)
      case "Wa Tor" => (WaTorEnvironment(100), WaTorEnvironment.colors)

    val pixelPanel = Gui((100, 100), colors)
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