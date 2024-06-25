package dsl.automaton

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.CellularAutomaton.State
import dsl.automaton.Neighbourhood2DBuilder.*
import domain.base.Position
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell

class DSLAutomatonBuilderTest extends AnyFunSuite:
  class Alive extends State:
    val name: String = "alive"

  class Dead extends State:
    val name: String = "dead"

  val alive = Alive()
  val dead = Dead()
  val nrb = Neighbourhood2DBuilder.configureNeighborhood:
    state(alive) | x | state(dead) | n |
    x            | c | x            | n |
    state(alive) | x | state(dead)

  test("The dsl should map a correct set of positions"):
    nrb.center.isEmpty should not be true
    nrb.center.get shouldBe Position(List(1, 1))

    val expectedPositions = List(
      (0, 0), (0, 2),
      (2, 0), (2, 2),
    ).map(x => Position(x.toList))

    val actualPositions = nrb.cells.map(_.position)

    actualPositions should contain theSameElementsAs expectedPositions

  test("The DSL should map a correct set of cells"):
    val expectedStates = (alive, dead, alive, dead).toList
    nrb.cells.map(_.state) should contain theSameElementsInOrderAs expectedStates

  test("Relative neighbours positions cannot be retrieved if center is not set"):
    val exc = intercept[IllegalStateException]:
      Neighbourhood2DBuilder.configureNeighborhood {
        state(alive) | state(dead) | state(alive)
      }.relativePositions 
    assert(!exc.getMessage().isBlank())

  test("Relative neighbours cells can be retrieved if center is set"):
    val rpos: List[Cell[TwoDimensionalSpace]] = nrb.relativePositions
    val expectedPositions: List[Cell[TwoDimensionalSpace]] = List(
      alive -> (-1, -1),
      dead -> (-1, 1),
      alive -> (1, -1),
      dead -> (1, 1),
    ).map (e => Cell(Position(e._2.toList), e._1))

    // rpos.map(_.position) should contain theSameElementsAs expectedPositions.map(_.position)
    rpos should contain theSameElementsAs expectedPositions
