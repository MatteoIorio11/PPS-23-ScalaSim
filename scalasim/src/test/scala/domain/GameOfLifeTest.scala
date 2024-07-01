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

abstract class GameOfLifeTest extends AnyFunSuite with BeforeAndAfterEach:
  def gameOfLife: CellularAutomaton[TwoDimensionalSpace]

  test("GameOfLife's map rule should not be empty"):
    gameOfLife.rules should not be (Map.empty)
  
  test("Alive cell should be Dead with less than two Alive neighbors"):
    val cell = Cell[TwoDimensionalSpace](Position(0, 0), CellState.ALIVE)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List(
      Cell(Position(1, 1), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position(0, 0), CellState.DEAD)

  test("Alive cell should be Dead with more than three Alive neighbors"):
    val cell = Cell[TwoDimensionalSpace](Position(1, 1), CellState.ALIVE)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List(
      Cell(Position(0, 1), CellState.ALIVE),
      Cell(Position(1, 0), CellState.ALIVE),
      Cell(Position(1, 2), CellState.ALIVE),
      Cell(Position(2, 1), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position(1, 1), CellState.DEAD)

  test("Alive cell should be Alive with two Alive neighbors"):
    val cell = Cell[TwoDimensionalSpace](Position(0, 1), CellState.ALIVE)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List(
      Cell(Position(1, 1), CellState.ALIVE),
      Cell(Position(0, 0), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position(0, 1), CellState.ALIVE)

  test("Alive cell should be Alive with three Alive neighbors"):
    val cell = Cell[TwoDimensionalSpace](Position(0, 1), CellState.ALIVE)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List(
      Cell(Position(0, 0), CellState.ALIVE),
      Cell(Position(0, 2), CellState.ALIVE),
      Cell(Position(1, 1), CellState.ALIVE),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    gameOfLife.applyRule(cell, neighbor) shouldBe Cell(Position(0, 1), CellState.ALIVE)

class BasicGOFImpl extends GameOfLifeTest:
  override val gameOfLife: CellularAutomaton[TwoDimensionalSpace] = GameOfLife()

class GameOfLifeBuilderTest extends GameOfLifeTest:
  // Any live cell with fewer than two live neighbours dies, as if by underpopulation.
  // Any live cell with two or three live neighbours lives on to the next generation.
  // Any live cell with more than three live neighbours dies, as if by overpopulation.
  // Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
  import domain.simulations.gameoflife.GameOfLife.CellState.ALIVE
  import domain.simulations.gameoflife.GameOfLife.CellState.DEAD
  import dsl.automaton.rule.DeclarativeRuleBuilder.*
  import dsl.automaton.rule.DeclarativeRuleBuilder.DSLExtensions.*
  import dsl.automaton.CellularAutomatonBuilder
  import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL.*
  import dsl.automaton.rule.DeclarativeRuleBuilder

  override def gameOfLife: CellularAutomaton[TwoDimensionalSpace] =
    CellularAutomatonBuilder.fromRuleBuilder {
      DeclarativeRuleBuilder.configureRules:
        DEAD when fewerThan(2) withState(ALIVE) whenCenterIs(ALIVE)
        ALIVE when surroundedBy(2) withState(ALIVE) whenCenterIs(ALIVE)
        ALIVE when surroundedBy(3) withState(ALIVE) whenCenterIs(ALIVE)
        DEAD when atLeastSurroundedBy(4) withState(ALIVE) whenCenterIs(ALIVE)
        ALIVE when surroundedBy(3) withState(ALIVE) whenCenterIs(DEAD)
    }.build()