package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.GameOfLife.*
import domain.CellularAutomata.*
import domain.Rule
import domain.Cell.*
import domain.Position.*
import org.scalatest.BeforeAndAfterEach

class CellularAutomataTest extends AnyFunSuite with BeforeAndAfterEach:
  val gameOfLife = GameOfLife()

  test("Cellular Automata's map rule should be empty"):
    gameOfLife.rules should be (Map())

  test("Add new rule for the Cellular Automata"):
    val state: State = CellularState.ALIVE
    val neighborRule: NeighborRule[TwoDimensionalSpace] = (x: Neighbor[TwoDimensionalSpace]) => 
      val y = NeighborRuleUtility.getNeighboursWithState(CellularState.ALIVE, x)
      val caller = x.center
      y.length match
        case 3 => Cell(caller.position.asPosition[Position2D], CellularState.ALIVE)
        case y if y <= 2 => Cell(caller.position.asPosition[Position2D], CellularState.DEAD)
        case x if x > 3 => Cell(caller.position.asPosition[Position2D], CellularState.DEAD)
    gameOfLife.addRule(state, neighborRule)
    gameOfLife.rules should not be (Map())
