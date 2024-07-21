package domain
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.exporter.SimpleMatrixToImageConverter
import domain.simulations.gameoflife.GameOfLife.CellState
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Color

class ExporterTest extends AnyFunSuite {

  test("convert should create image with correct dimensions and colors") {
    val matrix = LazyList(
      Cell[TwoDimensionalSpace](Position(0, 0), CellState.ALIVE),
      Cell[TwoDimensionalSpace](Position(0, 1), CellState.DEAD),
      Cell[TwoDimensionalSpace](Position(1, 0), CellState.DEAD),
      Cell[TwoDimensionalSpace](Position(1, 1), CellState.ALIVE)
    )

    val colors = Map[State, Color](
      CellState.ALIVE -> Color.BLACK,
      CellState.DEAD -> Color.WHITE
    )

    val cellSize = 10
    val image = SimpleMatrixToImageConverter.convert(matrix, cellSize, colors)

    assert(image.getWidth == 20)
    assert(image.getHeight == 20)

    assert(image.getRGB(0, 0) == Color.BLACK.getRGB)
    assert(image.getRGB(10, 0) == Color.WHITE.getRGB)
    assert(image.getRGB(0, 10) == Color.WHITE.getRGB)
    assert(image.getRGB(10, 10) == Color.BLACK.getRGB)
  }
}