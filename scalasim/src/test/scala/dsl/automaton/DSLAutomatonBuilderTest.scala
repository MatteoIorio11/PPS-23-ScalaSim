package dsl.automaton

import domain.automaton.CellularAutomaton.State
import domain.automaton.{Cell, Neighbour, NeighbourRule}
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import dsl.automaton.NeighbourRule2DBuilder.{*, given}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class DSLAutomatonBuilderTest extends AnyFunSuite:
  class Alive extends State:
    val name: String = "alive"

  class Dead extends State:
    val name: String = "dead"

  private val alive = Alive()
  private val dead = Dead()
  private val nrb = NeighbourRule2DBuilder.configureNeighborhood(dead):
    state(alive) | x        | state(dead) | n |
    x            | c(alive) | x            | n |
    state(alive) | x        | state(dead)

  test("The dsl should map a correct set of cells"):
    nrb.center.isEmpty should not be true
    nrb.center.get shouldBe Cell(Position(List(1, 1)), alive)

    val expectedPositions: List[Cell[TwoDimensionalSpace]] = List(
      (0, 0) -> alive,
      (0, 2) -> dead,
      (2, 0) -> alive,
      (2, 2) -> dead,
    ).map(x => Cell(Position(x._1.toList), x._2))

    nrb.cells should contain theSameElementsAs expectedPositions

  test("Relative neighbours positions cannot be retrieved if center is not set"):
    val exc = intercept[IllegalStateException]:
      NeighbourRule2DBuilder.configureNeighborhood(alive) {
        state(alive) | state(dead) | state(alive)
      }.relativePositions
    assert(!exc.getMessage.isBlank)

  private val expectedCells: List[Cell[TwoDimensionalSpace]] = List(
    alive -> (-1, -1),
    dead -> (-1, 1),
    alive -> (1, -1),
    dead -> (1, 1),
  ).map (e => Cell(Position(e._2.toList), e._1))

  test("Relative neighbours cells can be retrieved if center is set"):
    val rpos: List[Cell[TwoDimensionalSpace]] = nrb.relativePositions
    rpos should contain theSameElementsInOrderAs expectedCells

  test("The DSL should be able to produce a proper relative Neighbour instance"):
    val expectedNeighbourhood = Neighbour[TwoDimensionalSpace](
      Cell(Position((0, 0).toList), nrb.center.get.state), nrb.relativePositions
    )
    nrb.relativeNeighbourhood shouldBe expectedNeighbourhood

  test("Rule specified in DSL should work as expected"):
    val builder = NeighbourRule2DBuilder.configureNeighborhood(alive):
      state(alive) | c(dead) | state(alive)

    val center = Cell[TwoDimensionalSpace](Position((0, 1).toList), dead)
    val cells = List(
      (0, 0) -> alive,
      (0, 2) -> alive,
    ).map(x => Cell[TwoDimensionalSpace](Position(x._1.toList), x._2))

    val neighbourhood = Neighbour[TwoDimensionalSpace](center, cells)
    val expectedCell = Cell[TwoDimensionalSpace](Position((0, 1).toList), alive)

    val rule: NeighbourRule[TwoDimensionalSpace] = builder.rules.head
    builder.cells.foreach(println(_))
    builder.relativePositions.foreach(println(_))
    rule.applyTransformation(neighbourhood) shouldBe expectedCell

  test("Rule composition should be made available through `configureAnother`"):
    val builder = NeighbourRule2DBuilder.configureNeighborhood(alive) {
      state(alive) | c(dead) | state(alive)
    }.configureAnother(dead):
      state(dead) | c (alive) | state(dead)
    
    builder.rules.size shouldBe 2
