package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.CellularAutomata2D.*
import domain.CellularAutomata.*
import domain.Rule
import domain.Cell.*

class CellularAutomataTest extends AnyFunSuite:
  val cellularAutomata2D = CellularAutomata2D()

  test("Cellular Automata's map rule should be empty"):
    this.cellularAutomata2D.getRules should be (Map())

  test("Add new rule for the Cellular Automata"):
    val state: State = CellularState.ALIVE
    val neighborRule: NeighborRule[TwoDimensionalSpace] = (x: Neighbor[TwoDimensionalSpace]) => 
      val y = NeighborRuleUtility.getNeighboursWithState(CellularState.ALIVE, x)
      val caller = x.center
      y.length match
        case 3 => Cell2D(caller.position, CellularState.ALIVE)
        case y if y <= 2 => Cell2D(caller.position, CellularState.DEAD)
        case x if x > 3 => Cell2D(caller.position, CellularState.DEAD)
    this.cellularAutomata2D.addRule(state, neighborRule)
