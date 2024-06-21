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

class CellularAutomataTest extends AnyFunSuite with BeforeAndAfterEach:
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

  test("Apply rule on a specific cell should return the right cell"):
    val cell = Cell(Position2D((0,0).toList), CellState.DEAD)
    val state = CellState.DEAD
    val neighbors = List.empty
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    val rule: NeighbourRule[TwoDimensionalSpace] = (neighbor) =>
      Cell(Position((0,0).toList), CellState.ALIVE)
    gameOfLife.addRule(state, rule)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position((0,0).toList), CellState.ALIVE)

