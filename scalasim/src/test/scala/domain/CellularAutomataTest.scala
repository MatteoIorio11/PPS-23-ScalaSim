package domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.CellularAutomata.CellularAutomata
import domain.CellularAutomata2D

class CellularAutomataTest extends AnyFlatSpec:
  val cellularAutomata2D: CellularAutomata[TwoDimensionalSpace] = CellularAutomata2D[TwoDimensionalSpace]()

  test("Cellular Automata's map rule should be empty"):
    this.cellularAutomata2D.getRules should be (Map())
