package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.*
import domain.GameOfLife.*
import domain.automaton.CellularAutomata.*
import domain.automaton.Rule
import domain.automaton.Cell.*
import domain.base.Position.*
import org.scalatest.BeforeAndAfterEach
import domain.automaton.NeighborRuleUtility.NeighborhoodLocator
import domain.automaton.NeighborRuleUtility.given
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.Neighbor
import automaton.NeighborRule

class CellularAutomataTest extends AnyFunSuite with BeforeAndAfterEach:
  val gameOfLife = GameOfLife()

  test("Cellular Automata's map rule should be empty"):
    gameOfLife.rules should be (Map())

  test("Add new rule for the Cellular Automata"):
    val state: State = CellState.ALIVE
    val neighborRule: NeighborRule[TwoDimensionalSpace] = (x: Neighbor[TwoDimensionalSpace]) => 
      val y = automaton.NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x)
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
    val others = circleNeighborhoodLocator.relativeNeighborsLocations.map(pos => Cell(pos, CellState.DEAD))
    val neighbor: Neighbor[TwoDimensionalSpace] = Neighbor(cell, others)
    val rule: NeighborRule[TwoDimensionalSpace] = (neighbor) => 
      val y = 0
      Cell(Position((0,0).toList), CellState.DEAD)
    gameOfLife.addRule(state, rule)
    gameOfLife.neighboors(Cell(Position((0,0).toList), CellState.DEAD)) shouldBe neighbor.neighborhood.map(c => c.position)

  test("Apply rule on a specific cell should return the right cell"):
    val cell = Cell(Position2D((0,0).toList), CellState.DEAD)
    val state = CellState.DEAD
    val neighbors = List.empty
    val neighbor: Neighbor[TwoDimensionalSpace] = Neighbor(cell, neighbors)
    val rule: NeighborRule[TwoDimensionalSpace] = (neighbor) => 
      Cell(Position((0,0).toList), CellState.ALIVE)
    gameOfLife.addRule(state, rule)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position((0,0).toList), CellState.ALIVE)

