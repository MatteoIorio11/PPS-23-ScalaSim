package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.CellularAutomata.*

class CellularAutomataTest extends AnyFunSuite:
  val cellularAutomata2D: CellularAutomata[TwoDimensionalSpace] = CellularAutomata2D()

  test("Cellular Automata's map rule should be empty"):
    this.cellularAutomata2D.getRules should be (Map())
