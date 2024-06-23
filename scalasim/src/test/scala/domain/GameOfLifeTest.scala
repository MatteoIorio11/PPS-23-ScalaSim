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

  test("GameOfLife's map rule should not be empty"):
    gameOfLife.rules should not be (Map.empty)
  
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

  test("Alive cell should be Alive with three Alive neighbors"):
    val cell = Cell(Position2D((0, 0).toList), CellState.ALIVE)
    val neighbors = List(
      Cell(Position2D((1, 1).toList), CellState.ALIVE),
      Cell(Position2D((1, -1).toList), CellState.ALIVE),
      Cell(Position2D((-1, -1).toList), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position((0, 0).toList), CellState.ALIVE)