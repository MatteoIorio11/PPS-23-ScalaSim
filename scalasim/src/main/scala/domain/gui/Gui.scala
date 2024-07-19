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
import domain.simulations.WaTorCellularAutomaton.WatorState.{Fish, Shark}
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.simulations.gameoflife.GameOfLife.CellState as GameOfLifeState
import domain.simulations.langtonsant.LangtonsAntEnvironment

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JComboBox, JFrame, JLabel, JOptionPane, JPanel, JSlider, JTextField}
import scala.collection.immutable.LazyList
case class EnvironmentOption[D <: Dimension, R](name: String, createEnvironment: (Int, Int, Map[? <: State, Int]) => GenericEnvironment[D, R], colors: Map[State, Color], isToroidal: Boolean, states: List[State])

object EnvironmentOption:
  val options = List(
    EnvironmentOption("Brian's Brain", (width, height, initialCells) => BriansBrainEnvironment(width, initialCells), BriansBrainEnvironment.colors, false, List(CellState.ON, CellState.OFF, CellState.DYING)),
    EnvironmentOption("Game of Life", (width, height, initialCells) => GameOfLifeEnvironment(width, height, initialCells), GameOfLifeEnvironment.colors, true, List(GameOfLifeState.ALIVE, GameOfLifeState.DEAD)),
    EnvironmentOption("Wa Tor", (width, height, initialCells) => WaTorEnvironment(width, height, initialCells), WaTorEnvironment.colors, true, List(Fish(), Shark())),
    EnvironmentOption("Langton's Ant", (width, height, initialCells) => LangtonsAntEnvironment(width), LangtonsAntEnvironment.colors, true, List())
  )

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

object GuiRunner:
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
    var stateFields: Map[State, (JLabel, JTextField)] = Map()

    def updateStateFields(environmentOption: EnvironmentOption[?, ?]): Unit = {
      stateFields.values.foreach { case (label, field) =>
        frame.remove(label)
        frame.remove(field)
      }

      stateFields = environmentOption.states.zipWithIndex.map { case (state, index) =>
        val label = JLabel(s"$state:")
        val field = JTextField("0")
        label.setBounds(50, 350 + index * 40, 100, 30)
        field.setBounds(150, 350 + index * 40, 100, 30)
        frame.add(label)
        frame.add(field)
        (state, (label, field))
      }.toMap

      frame.revalidate()
      frame.repaint()
    }

    comboBox.addActionListener(_ =>
      val selectedOption = comboBox.getSelectedItem.toString
      val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get

      if environmentOption.isToroidal then
        frame.add(heightSlider)
        frame.add(heightLabel)
      else
        frame.remove(heightSlider)
        frame.remove(heightLabel)

      updateStateFields(environmentOption)

      frame.revalidate()
      frame.repaint()
    )

    widthSlider.addChangeListener(_ =>
      widthLabel.setText(s"Width: ${widthSlider.getValue}")
    )

    heightSlider.addChangeListener(_ =>
      heightLabel.setText(s"Height: ${heightSlider.getValue}")
    )

    startButton.addActionListener(_ =>
      try{
        guiE.foreach(guiEngine => guiEngine.stopEngine)
        currentPanel.foreach(panel => frame.remove(panel))

        val selectedOption = comboBox.getSelectedItem.toString
        val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get
        val width = widthSlider.getValue()
        val height = if environmentOption.isToroidal then heightSlider.getValue() else width

        val initialCells = stateFields.map { case (state, (_, field)) =>
          state -> field.getText.toInt
        }

        val env = environmentOption.createEnvironment(width, height, initialCells)
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
      } catch {
        case ex: Exception =>
          JOptionPane.showMessageDialog(frame, s"Bad environment configuration", "Error", JOptionPane.ERROR_MESSAGE)
      }
    )

    stopButton.addActionListener(_ =>
      guiE.foreach(guiEngine => guiEngine.stopEngine)
      currentPanel.foreach(panel => frame.remove(panel))
      currentPanel = None
      frame.revalidate()
      frame.repaint()
    )

    exportButton.addActionListener(_ => {
      try {
        val selectedOption = comboBox.getSelectedItem.toString
        val environmentOption = EnvironmentOption.options.find(_.name == selectedOption).get
        val width = widthSlider.getValue()
        val height = if environmentOption.isToroidal then heightSlider.getValue() else width

        val initialCells = stateFields.map { case (state, (_, field)) =>
          state -> field.getText.toInt
        }

        val env = environmentOption.createEnvironment(width, height, initialCells)
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
      } catch {
        case ex: Exception =>
          JOptionPane.showMessageDialog(frame, s"Bad environment configuration", "Error", JOptionPane.ERROR_MESSAGE)
      }
    })

    exitButton.addActionListener(_ =>
      guiE.foreach(guiEngine => guiEngine.stopEngine)
      currentPanel.foreach(panel => frame.remove(panel))
      frame.dispose())
