package domain

import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.simulations.gameoflife.GameOfLife
import automaton.Rule
import domain.automaton.NeighbourRule
import utility.DummyAutomaton.DummyState.ALIVE
import utility.DummyAutomaton.DummyState.DEAD
import domain.automaton.CellularAutomaton.AnyState
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.Neighbour
import domain.automaton.NeighborRuleUtility.getNeighboursWithState

class RuleTest extends org.scalatest.funsuite.AnyFunSuite:

  test("An identity Rule should return the same input"):
    val r: Rule[Any, Any, Any] = x => x
    val ca = GameOfLife()

    r.applyTransformation(ca) shouldBe ca
    r.applyTransformation((1234, "1234")) shouldBe (1234, "1234")

  test("A rule can represent any functions"):
    val r: Rule[Int, String, Any] = x => s"${x} as a string"
    r.applyTransformation(10) shouldBe "10 as a string"

  test("A basic NeighbourRule should apply transformations"):
    val invertStateRule: NeighbourRule[TwoDimensionalSpace] = NeighbourRule(Some(AnyState)): n =>
      n.center.state match
        case ALIVE => Cell(n.center.position, DEAD)
        case DEAD => Cell(n.center.position, ALIVE)

    val aliveCell = Cell[TwoDimensionalSpace](Position(0, 0), ALIVE)
    val deadCell = Cell[TwoDimensionalSpace](Position(0, 0), DEAD)

    invertStateRule.applyTransformation(Neighbour(aliveCell, List.empty)) shouldBe deadCell
    invertStateRule.applyTransformation(Neighbour(deadCell, List.empty)) shouldBe aliveCell

  test("A NeighbourRule applied to a neighbourhood should apply a transformation"):
    val nRule: NeighbourRule[TwoDimensionalSpace] = NeighbourRule(Some(ALIVE)): n =>
      if getNeighboursWithState(DEAD, n).size >= 2  then Cell(n.center.position, DEAD) else n.center

    val center = Cell[TwoDimensionalSpace](Position(1, 1), ALIVE)
    val neighbourhood: Neighbour[TwoDimensionalSpace] = Neighbour(
      center,
      List(
        Cell(Position(0, 0), DEAD),
        Cell(Position(0, 2), ALIVE),
        Cell(Position(2, 1), DEAD),
      )
    )

    nRule.applyTransformation(neighbourhood).state shouldBe DEAD
