package domain.gui

import domain.automaton.Cell
import domain.base.Dimensions.TwoDimensionalSpace
import domain.engine.Engine.EngineView

import java.awt.{Color, Graphics}
import javax.swing.JPanel
import domain.automaton.CellularAutomaton.State

class PixelPanel(val dimension: (Int, Int), colors: Map[State, Color]) extends JPanel with EngineView[TwoDimensionalSpace]:
  private var pixels: LazyList[Cell[TwoDimensionalSpace]] = LazyList()
  private var h = dimension._1
  private var w = dimension._2
  private var ps = 5

  def setPixelSize(newSize: Int): Unit =
    ps = newSize

  override def updateView(cells: Iterable[Cell[TwoDimensionalSpace]]) =
    pixels = LazyList(cells.toSeq *)
    repaint()

  override def paintComponent(g: Graphics) =
    super.paintComponent(g)
    pixels.foreach(cell =>
      val color = colors.getOrElse(cell.state, Color.WHITE)
      val pos = cell.position
      g.setColor(color)
      g.fillRect(pos.coordinates.head * ps, pos.coordinates.last * ps, ps, ps)
    )