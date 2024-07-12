package domain.gui

import domain.automaton.Cell
import domain.Environment.{ArrayToroidEnvironment, GenericEnvironment}
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine.Engine.{EngineView, GUIEngine2D}
import domain.engine.{Engine2D, GUIEngine2D}
import domain.exporter.{Exporter, JCodecVideoGenerator, SimpleMatrixToImageConverter}
import domain.simulations.briansbrain.BriansBrainEnvironment
import domain.simulations.gameoflife.GameOfLifeEnvironment
import domain.simulations.* 
import domain.simulations.langtonsant.LangtonsAntEnvironment

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JComboBox, JFrame, JLabel, JOptionPane, JPanel, JSlider}
import scala.collection.immutable.LazyList

case class EnvironmentOption[D <: Dimension, R](name: String, createEnvironment: (Int, Int) => GenericEnvironment[D, R], colors: Map[State, Color], isToroidal: Boolean)

object EnvironmentOption:
  val options = List(
    EnvironmentOption("Brian's Brain", (width, height) => BriansBrainEnvironment(width), BriansBrainEnvironment.colors, false),
    EnvironmentOption("Game of Life", GameOfLifeEnvironment.apply, GameOfLifeEnvironment.colors, true),
    EnvironmentOption("Wa Tor", WaTorEnvironment.apply, WaTorEnvironment.colors, true), 
    EnvironmentOption("Langton's Ant", (width, height) => LangtonsAntEnvironment(width), LangtonsAntEnvironment.colors, true)
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
  val exportButton = JButton("Export Video")
  val comboBox = JComboBox(EnvironmentOption.options.map(_.name).toArray)
  val widthSlider = JSlider(10, 200, 100)
  val heightSlider = JSlider(10, 200, 100)
  val widthLabel = JLabel(s"Width: ${widthSlider.getValue}")
  val heightLabel = JLabel(s"Height: ${heightSlider.getValue}")

  frame.setLayout(null)
  comboBox.setBounds(50, 50, 200, 30)
  widthSlider.setBounds(50, 150, 200, 50)
  widthLabel.setBounds(260, 150, 100, 50)
  heightSlider.setBounds(50, 200, 200, 50)
  heightLabel.setBounds(260, 200, 100, 50)
  startButton.setBounds(50, 100, 100, 30)
  stopButton.setBounds(150, 100, 100, 30)
  exportButton.setBounds(50, 250, 200, 30)
  exitButton.setBounds(50, 300, 100, 30)

  frame.add(comboBox)
  frame.add(startButton)
  frame.add(stopButton)
  frame.add(exitButton)
  frame.add(exportButton)
  frame.add(widthSlider)
  frame.add(widthLabel)

  frame.setSize(1000, 600)
  frame.setDefaultCloseOperation(3)
  frame.setVisible(true)

  var guiE: Option[GUIEngine2D] = None
  var currentPanel: Option[Gui] = None

  comboBox.addActionListener(_ =>
    val selectedOption = comboBox.getSelectedItem.toString
    val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get

    if environmentOption.isToroidal then
      frame.add(heightSlider)
      frame.add(heightLabel)
    else
      frame.remove(heightSlider)
      frame.remove(heightLabel)

    frame.revalidate()
    frame.repaint()
  )

  widthSlider.addChangeListener(_ =>
    widthLabel.setText(s"Width: ${widthSlider.getValue}")
  )

  heightSlider.addChangeListener(_ =>
    heightLabel.setText(s"Height: ${heightSlider.getValue}")
  )

  startButton.addActionListener(e =>
    guiE.foreach(guiEngine => guiEngine.stopEngine)
    currentPanel.foreach(panel => frame.remove(panel))

    val selectedOption = comboBox.getSelectedItem.toString
    val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get
    val width = widthSlider.getValue()
    val height = if environmentOption.isToroidal then heightSlider.getValue() else width

    val env = environmentOption.createEnvironment(width, height)
    val colors = environmentOption.colors

    val pixelPanel = Gui((width, height), colors)
    val panelSize = 500
    val pixelSize = panelSize / math.max(width, height)
    pixelPanel.setPixelSize(pixelSize)

    guiE = Some(GUIEngine2D(env, pixelPanel))
    currentPanel = Some(pixelPanel)

    pixelPanel.setBounds(400, 50, 500, 500)
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

  exportButton.addActionListener(e => {
    val selectedOption = comboBox.getSelectedItem.toString
    val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get
    val width = widthSlider.getValue()
    val height = if (environmentOption.isToroidal) heightSlider.getValue() else width

    val env = environmentOption.createEnvironment(width, height)
    val engine = Engine2D(env, 5)
    engine.startEngine
    Thread.sleep(2000)
    engine.stopEngine

    Exporter.exportMatrix(
      engine,
      environmentOption.colors,
      converter = SimpleMatrixToImageConverter,
      videoGenerator = JCodecVideoGenerator,
      cellSize = 10,
      videoFilename = "output.mp4",
      secondsPerImage = 0.1
    )

    JOptionPane.showMessageDialog(frame, "Video exported as output.mp4")
  })

  exitButton.addActionListener(e =>
    guiE.foreach(guiEngine => guiEngine.stopEngine)
    currentPanel.foreach(panel => frame.remove(panel))
    frame.dispose())
