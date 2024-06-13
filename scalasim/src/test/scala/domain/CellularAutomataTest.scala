package domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class CellularAutomataTest extends AnyFlatSpec:
  val cellularAutomata2D: CellularAutomata[TwoDimensionalSpace] = CellularAutomata2D[TwoDimensionalSpace]()

  test("Cellular Automata's map rule should be empty"):
    ???
