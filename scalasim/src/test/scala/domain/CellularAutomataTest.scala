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
    val state: State = CellState.ALIVE
    val neighborRule: NeighborRule[TwoDimensionalSpace] = (x: Neighbor[TwoDimensionalSpace]) => 
      val y = NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x)
      val caller = x.center
      y.length match
        case 3 => Cell(caller.position.asPosition[Position2D], CellState.ALIVE)
        case y if y <= 2 => Cell(caller.position.asPosition[Position2D], CellState.DEAD)
        case x if x > 3 => Cell(caller.position.asPosition[Position2D], CellState.DEAD)
    gameOfLife.addRule(state, neighborRule)
    gameOfLife.rules should not be (Map())

  test("Given a Cell we should be able to retrieve the correct neighboors"):
    val cell = Cell(Position2D((0, 0).toList), CellState.DEAD) 
    val state: State = CellState.ALIVE
    val others = List(
        Cell(Position2D((0, 1).toList), CellState.DEAD),
        Cell(Position2D((1, 0).toList), CellState.DEAD),
        Cell(Position2D((1, 0).toList), CellState.DEAD),
        Cell(Position2D((-1, 0).toList), CellState.DEAD),
        Cell(Position2D((0, -1).toList), CellState.DEAD),
        Cell(Position2D((1, -1).toList), CellState.DEAD),
        Cell(Position2D((-1, 1).toList), CellState.DEAD),
        Cell(Position2D((-1, -1).toList), CellState.DEAD),
      ).toIterable
    val neighbor: Neighbor[TwoDimensionalSpace] = Neighbor(cell, others)
    val rule: NeighborRule[TwoDimensionalSpace] = (neighbor) => 
      val y = 0
      Cell(Position((0,0).toList), CellState.DEAD)
    gameOfLife.addRule(state, rule)
    gameOfLife.neighboors(Cell(Position((0,0).toList), CellState.DEAD)) should be (neighbor.neighborhood)


