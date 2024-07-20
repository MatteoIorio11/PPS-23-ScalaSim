package domain.simulations

import domain.automaton.Cell
import domain.automaton.Neighbour
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.simulations.langtonsant.LangtonsAntAutomaton
import domain.simulations.langtonsant.LangtonsAntAutomaton.CellState.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.simulations.langtonsant.LangtonsAntAutomaton.LangstonAntState.ANT
import domain.automaton.NeighborRuleUtility.RelativePositions.*
import domain.automaton.NeighborRuleUtility.getCircularNeighbourhoodPositions
import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
import domain.simulations.langtonsant.LangtonsAntAutomaton.CellState

class LangtonsAntTest extends AnyFunSuite:

  private val ca: LangtonsAntAutomaton = LangtonsAntAutomaton()

  test("A Langton's Ant Cellular Automaton should do nothing on non-ant cells"):
    val whiteNeighbourhood: Neighbour[TwoDimensionalSpace] = Neighbour(
      center = Cell(Position(0, 0), WHITE),
      List.empty,
    )
    
    val blackNeighbourhood: Neighbour[TwoDimensionalSpace] = Neighbour(
      center = Cell(Position(0, 0), WHITE),
      List.empty,
    )

    ca.applyRule(whiteNeighbourhood) shouldBe List.empty
    ca.applyRule(blackNeighbourhood) shouldBe List.empty
    
  test("The ant rule should yield the old position and a new position"):
    val ant = Cell[TwoDimensionalSpace](Position(1, 1), ANT(WHITE, South))
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      ant,
      getCircularNeighbourhoodPositions()
        .absoluteNeighboursLocations(Position(1, 1))
        .map(p => Cell(p, WHITE))
    )

    val (oldCell, newCell) = unapplyNeighbourRule(ca.applyRule(n))

    oldCell.position shouldBe ant.position
    oldCell.state shouldBe ant.state.asInstanceOf[ANT].cellColor.invert
    newCell.position should not be ant.position
    newCell.state.isInstanceOf[ANT] shouldBe true

  test("Ant on a white cell should behave as expected"):
    def testRule(n: Neighbour[TwoDimensionalSpace], expectedState: CellState, expectedPos: Position[TwoDimensionalSpace]): Unit =
      var (oldCell, newCell) = unapplyNeighbourRule(ca.applyRule(n))
      oldCell.state shouldBe expectedState
      newCell.position shouldBe expectedPos

    // At a white square, turn 90° clockwise, flip the color of the square, move forward one unit
    val neighbours = getCircularNeighbourhoodPositions()
        .absoluteNeighboursLocations(Position(1, 1))
        .map(p => Cell(p, WHITE))

    val nWhite: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell[TwoDimensionalSpace](Position(1, 1), ANT(WHITE, South)),
      neighbours
    )

    val nBlack: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell[TwoDimensionalSpace](Position(1, 1), ANT(BLACK, South)),
      neighbours
    )

    testRule(nWhite, BLACK, nWhite.center.position + West)
    testRule(nBlack, WHITE, nWhite.center.position + East)


  private def unapplyNeighbourRule(in: Iterable[Cell[TwoDimensionalSpace]]): (Cell[TwoDimensionalSpace], Cell[TwoDimensionalSpace]) = in match
    case c1 :: c2 :: rest => (c1, c2)
    case _ => fail()
  