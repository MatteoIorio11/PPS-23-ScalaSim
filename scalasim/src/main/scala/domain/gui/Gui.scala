package domain.gui

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.TwoDimensionalSpace
import domain.engine.Engine.{EngineView, GUIEngine2D}
import domain.engine.GUIEngine2D
import domain.simulations.briansbrain.BriansBrainEnvironment
import domain.simulations.gameoflife.GameOfLifeEnvironment

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JFrame, JPanel, JComboBox, JSlider, WindowConstants}
import scala.collection.immutable.LazyList

class Gui(val dimension: (Int, Int), colors: Map[State, Color]) extends JPanel with EngineView[TwoDimensionalSpace]:
  private var pixels: LazyList[Cell[TwoDimensionalSpace]] = LazyList()
  private var h = dimension._1
  private var w = dimension._2
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
  val automatonOptions = Array("Brian's Brain", "Game of Life")
  val comboBox = JComboBox(automatonOptions)
  val sizeSlider = JSlider(10, 200, 100)

  frame.setLayout(null)
  comboBox.setBounds(50, 50, 200, 30)
  sizeSlider.setBounds(50, 150, 200, 50)
  startButton.setBounds(50, 250, 100, 30)
  stopButton.setBounds(150, 250, 100, 30)
  exitButton.setBounds(50, 350, 100, 30)

  frame.add(comboBox)
  frame.add(sizeSlider)
  frame.add(startButton)
  frame.add(stopButton)
  frame.add(exitButton)

  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setVisible(true)

  var guiE: Option[GUIEngine2D] = None
  var currentPanel: Option[Gui] = None

  startButton.addActionListener(e =>
    guiE.foreach(guiEngine => guiEngine.stopEngine)
    currentPanel.foreach(panel => frame.remove(panel))

    val selected = comboBox.getSelectedItem.toString
    val size = sizeSlider.getValue()
    val (env, colors) = selected match
      case "Brian's Brain" => (BriansBrainEnvironment(size), BriansBrainEnvironment.colors)
      case "Game of Life" => (GameOfLifeEnvironment(size), GameOfLifeEnvironment.colors)

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
    frame.dispose()
  )
