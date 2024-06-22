package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.*
import domain.simulations.gameoflife.GameOfLife.*
import domain.automaton.CellularAutomaton.*
import domain.automaton.Rule
import domain.automaton.Cell.*
import domain.base.Position.*
import org.scalatest.BeforeAndAfterEach
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.NeighborRuleUtility.given
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.Neighbour
import automaton.NeighbourRule
import simulations.gameoflife.GameOfLife
import domain.automaton.NeighborRuleUtility

class GameOfLifeTest extends AnyFunSuite with BeforeAndAfterEach:
  val gameOfLife = GameOfLife()

  test("Cellular Automata's map rule should be empty"):
    gameOfLife.rules should be (Map())

  test("Add new rule for the Cellular Automata"):
    val state: State = CellState.ALIVE
    val neighborRule: NeighbourRule[TwoDimensionalSpace] = (x: Neighbour[TwoDimensionalSpace]) =>
      val y = NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x)
      val caller = x.center
      y.length match
        case 3 => Cell(caller.position.asPosition[Position2D], CellState.ALIVE)
        case y if y <= 2 => Cell(caller.position.asPosition[Position2D], CellState.DEAD)
        case x if x > 3 => Cell(caller.position.asPosition[Position2D], CellState.DEAD)
    gameOfLife.addRule(state, neighborRule)
    gameOfLife.rules should not be (Map())

  test("Alive cell should be Dead with less than two Alive neighbors"):
    val cell = Cell(Position2D((0,0).toList), CellState.ALIVE)
    val neighbors = List(
      Cell(Position2D((1, 1).toList), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position((0,0).toList), CellState.DEAD)

  test("Alive cell should be Dead with more than three Alive neighbors"):
    val cell = Cell(Position2D((0,0).toList), CellState.ALIVE)
    val neighbors = List(
      Cell(Position2D((1, 1).toList), CellState.ALIVE),
      Cell(Position2D((-1, 1).toList), CellState.ALIVE),
      Cell(Position2D((-1, -1).toList), CellState.ALIVE),
      Cell(Position2D((0, 1).toList), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position((0,0).toList), CellState.DEAD)

  test("Alive cell should be Alive with two Alive neighbors"):
    val cell = Cell(Position2D((0,0).toList), CellState.ALIVE)
    val neighbors = List(
      Cell(Position2D((1, 1).toList), CellState.ALIVE),
      Cell(Position2D((1, -1).toList), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position((0,0).toList), CellState.ALIVE)
