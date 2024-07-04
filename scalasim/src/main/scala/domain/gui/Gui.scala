package domain.gui

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.TwoDimensionalSpace
import domain.engine.Engine.EngineView
import domain.engine.GUIEngine2D
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.simulations.briansbrain.BriansBrainEnvironment

import java.awt.{Color, Graphics}
import javax.swing.{JButton, JFrame, JPanel}

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
