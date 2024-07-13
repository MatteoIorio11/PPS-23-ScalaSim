package domain.simulations

import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
import domain.automaton.NeighborRuleUtility.RelativePositions.*
import domain.automaton.NeighborRuleUtility.getCircularNeighbourhoodPositions
import domain.automaton.{Cell, Neighbour}
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.simulations.briansbrain.BriansBrain
import domain.simulations.briansbrain.BriansBrain.CellState
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class BriansBrainTest extends AnyFunSuite:

  private val ca = BriansBrain()

  test("A cell that was ON goes into DYING state"):
    val cell = Cell[TwoDimensionalSpace](Position(0, 0), CellState.ON)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List()
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    ca.applyRule(neighbor) shouldBe Cell(Position(0, 0), CellState.DYING)

  test("A cell that was OFF with exactly 2 ON neighbours goes into ON state"):
    val cell = Cell[TwoDimensionalSpace](Position(0, 0), CellState.OFF)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List(
      Cell(Position(1, 1), CellState.ON),
      Cell(Position(0, 1), CellState.ON),
    )
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    ca.applyRule(neighbor) shouldBe Cell(Position(0, 0), CellState.ON)

  test("A cell that was DYING goes into OFF state"):
    val cell = Cell[TwoDimensionalSpace](Position(0, 0), CellState.DYING)
    val neighbors: List[Cell[TwoDimensionalSpace]] = List()
    val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
    ca.applyRule(neighbor) shouldBe Cell(Position(0, 0), CellState.OFF)

    