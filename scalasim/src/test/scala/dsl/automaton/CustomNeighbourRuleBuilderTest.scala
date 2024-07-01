package dsl.automaton.rule

import domain.automaton.CellularAutomaton.State
import domain.automaton.{Cell, Neighbour, NeighbourRule}
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.{*, given}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import utility.DummyAutomaton

class CustomNeighbourRuleBuilderTest extends AnyFunSuite:
  private val alive = DummyAutomaton.DummyState.ALIVE
  private val dead = DummyAutomaton.DummyState.DEAD

  private val nrb = ExplicitNeighbourRuleBuilder.configureRule(dead):
    state(alive) | x        | state(dead) | n |
    x            | c(alive) | x            | n |
    state(alive) | x        | state(dead)

  test("The dsl should map a correct set of cells"):
    nrb.center.isEmpty should not be true
    nrb.center.get shouldBe Cell(Position(1, 1), alive)

    val expectedPositions: List[Cell[TwoDimensionalSpace]] = List(
      Position[TwoDimensionalSpace](0, 0) -> alive,
      Position[TwoDimensionalSpace](0, 2) -> dead,
      Position[TwoDimensionalSpace](2, 0) -> alive,
      Position[TwoDimensionalSpace](2, 2) -> dead,
    ).map(x => Cell(x._1, x._2))

    nrb.cells should contain theSameElementsAs expectedPositions

  test("Relative neighbours positions cannot be retrieved if center is not set"):
    val exc = intercept[IllegalStateException]:
       ExplicitNeighbourRuleBuilder.configureRule(alive) {
        state(alive) | state(dead) | state(alive)
      }.relativePositions
    assert(!exc.getMessage.isBlank)

  private val expectedCells: List[Cell[TwoDimensionalSpace]] = List(
    alive -> Position[TwoDimensionalSpace](-1, -1),
    dead  -> Position[TwoDimensionalSpace](-1, 1),
    alive -> Position[TwoDimensionalSpace](1, -1),
    dead  -> Position[TwoDimensionalSpace](1, 1),
  ).map (e => Cell(e._2, e._1))

  test("Relative neighbours cells can be retrieved if center is set"):
    val rpos: List[Cell[TwoDimensionalSpace]] = nrb.relativePositions
    rpos should contain theSameElementsInOrderAs expectedCells

  test("The DSL should be able to produce a proper relative Neighbour instance"):
    val expectedNeighbourhood = Neighbour[TwoDimensionalSpace](
      Cell(Position(0, 0), nrb.center.get.state), nrb.relativePositions
    )
    nrb.relativeNeighbourhood shouldBe expectedNeighbourhood

  test("Rule specified in DSL should work as expected"):
    val builder = ExplicitNeighbourRuleBuilder.configureRule(alive):
      state(alive) | c(dead) | state(alive)

    val center = Cell[TwoDimensionalSpace](Position(0, 1), dead)
    val cells = List(
      Position[TwoDimensionalSpace](0, 0) -> alive,
      Position[TwoDimensionalSpace](0, 2) -> alive,
    ).map(x => Cell[TwoDimensionalSpace](x._1, x._2))

    val neighbourhood = Neighbour[TwoDimensionalSpace](center, cells)
    val expectedCell = Cell[TwoDimensionalSpace](Position(0, 1), alive)

    val rule: NeighbourRule[TwoDimensionalSpace] = builder.rules.head
    rule.applyTransformation(neighbourhood) shouldBe expectedCell

  test("Rule composition should be made available through `configureAnother`"):
    val builder = ExplicitNeighbourRuleBuilder.configureRule(alive) {
      state(alive) | c(dead) | state(alive)
    }.configureAnother(dead):
      state(dead) | n |
      c(alive)   | n |
      state(dead)
    
    builder.rules.size shouldBe 2

    val aliveNeighbourhood = Neighbour[TwoDimensionalSpace](
      center = Cell(Position(1, 1), dead),
      List(
        Position[TwoDimensionalSpace](1, 0) -> alive,
        Position[TwoDimensionalSpace](1, 2) -> alive,
      ).map(x => Cell(x._1, x._2))
    )

    val deadNeighbourhood = Neighbour[TwoDimensionalSpace](
      center = Cell(Position(1, 0), alive),
      List(
        Position[TwoDimensionalSpace](0, 0) -> dead,
        Position[TwoDimensionalSpace](2, 0) -> dead,
      ).map(x => Cell(x._1, x._2))
    )

    builder.rules.toList(0).applyTransformation(aliveNeighbourhood) shouldBe Cell[TwoDimensionalSpace](Position(1, 1), alive)
    builder.rules.toList(1).applyTransformation(deadNeighbourhood) shouldBe Cell[TwoDimensionalSpace](Position(1, 0), dead)

  test("Complex rule should hold"):
    val builder = ExplicitNeighbourRuleBuilder.configureRule(dead):
      state(dead) | x        | state(dead) | n |
      x           | c(alive) | x           | n |
      state(dead) | x        | state(dead)
    
    val deadNeighbourhood = Neighbour[TwoDimensionalSpace](
        Cell(Position(1, 1), alive),
        List(
          Position[TwoDimensionalSpace](0, 0) -> dead,
          Position[TwoDimensionalSpace](2, 2) -> dead,
          Position[TwoDimensionalSpace](0, 2) -> dead,
          Position[TwoDimensionalSpace](2, 0) -> dead,
          Position[TwoDimensionalSpace](0, 1) -> alive,
          Position[TwoDimensionalSpace](1, 0) -> alive,
          Position[TwoDimensionalSpace](1, 2) -> alive,
          Position[TwoDimensionalSpace](2, 1) -> alive,
        ).map(x => Cell(x._1, x._2))
    )

    builder.rules.head.applyTransformation(deadNeighbourhood) shouldBe Cell(Position(1, 1), dead)
