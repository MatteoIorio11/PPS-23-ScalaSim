package dsl.automaton

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.CellularAutomaton.State
import dsl.automaton.DeclarativeRuleBuilder.*
import dsl.automaton.DeclarativeRuleBuilder.DSLExtensions.*
import dsl.automaton.ExplicitNeighbourRuleBuilder.*
import domain.automaton.Cell
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.base.Position
import utility.DummyAutomaton

class DeclarativeNeighbourRuleBuilderTest extends AnyFunSuite:
  private val dead = DummyAutomaton.DummyState.DEAD
  private val alive = DummyAutomaton.DummyState.ALIVE

  test("DSL syntax should work as expected"):
    DeclarativeRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)
      alive when atLeastSurroundedBy(2) withState(alive)

  test("The builder should be capable of bulding a rule through the DSL"):
    val builder = DeclarativeRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)

    builder.build
    builder.rules.size shouldBe 1

  test("The DSL should map a correct rule"):
    val builder = DeclarativeRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)

    builder.build
    val aliveRule = builder.rules.head

    val aliveNeighbourhood = Neighbour[TwoDimensionalSpace](
        Cell(Position(1, 0), dead),
        List(
          Cell(Position(0, 0), alive),
          Cell(Position(2, 0), alive),
        ),
    )

    val aliveCell = Cell(Position(1, 0),  alive)
    aliveRule.applyTransformation(aliveNeighbourhood) shouldBe aliveCell

  test("The DSL shoudl map a correct rule even without optional parameters"):
    val builder = DeclarativeRuleBuilder.configureRules:
      dead when atLeastSurroundedBy(1) withState(dead)

    builder.build
    val rule = builder.rules.head

    val n = Neighbour[TwoDimensionalSpace](
        Cell(Position(1, 0), alive),
        List(
          Cell(Position(0, 0), alive),
          Cell(Position(2, 0), dead),
        ),
    )

    val expected = Cell(Position(1, 0), dead)
    rule.applyTransformation(n) shouldBe expected

  private object RulesTestUtils:
      val aliveNeighbourhood = Neighbour[TwoDimensionalSpace](
          Cell(Position(1, 0), dead),
          List(
            Cell(Position(0, 0), alive),
            Cell(Position(2, 0), alive),
          ),
      )

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

      val aliveCell = Cell(Position(1, 0), alive)
      val deadCell = Cell(Position(1, 1), dead)

  test("It should be possible to configure multiple rules in one configuration block"):
    val builder = DeclarativeRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) whenCenterIs(dead)
      dead when surroundedBy(4) withState(dead)

    builder.build
    
    builder.rules.size shouldBe 2

    val aliveRule = builder.rules.head
    val deadRule = builder.rules.last
    
    aliveRule.applyTransformation(RulesTestUtils.aliveNeighbourhood) shouldBe RulesTestUtils.aliveCell
    deadRule.applyTransformation(RulesTestUtils.deadNeighbourhood) shouldBe RulesTestUtils.deadCell
    
  test("It should be possible to specify an exact neighbour configuration as a rule"):
    val builder = DeclarativeRuleBuilder.configureRules:
        dead whenNeighbourhoodIsExactlyLike:
          state(dead) | x        | state(dead) | n |
          x           | c(alive) | x           | n |
          state(dead) | x        | state(dead)

    val deadRule = builder.build.head
    deadRule.applyTransformation(RulesTestUtils.deadNeighbourhood) shouldBe RulesTestUtils.deadCell

  test("Explicit and Declartive rules should be concatened"):
    val builder = DeclarativeRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) whenCenterIs(dead)
      dead whenNeighbourhoodIsExactlyLike:
          state(dead) | x        | state(dead) | n |
          x           | c(alive) | x           | n |
          state(dead) | x        | state(dead)

    builder.build

    val aliveRule = builder.rules.last
    val deadRule = builder.rules.head
    aliveRule.applyTransformation(RulesTestUtils.aliveNeighbourhood) shouldBe RulesTestUtils.aliveCell
    deadRule.applyTransformation(RulesTestUtils.deadNeighbourhood) shouldBe RulesTestUtils.deadCell

  test("`fewerThan` should work as expected"):
    val neighbourhood: Neighbour[TwoDimensionalSpace] = Neighbour(
      center = Cell(Position(1, 1), alive),
      List(
        Position[TwoDimensionalSpace](0, 0) -> dead,
        Position[TwoDimensionalSpace](0, 1) -> alive,
        Position[TwoDimensionalSpace](0, 2) -> alive,
        Position[TwoDimensionalSpace](1, 0) -> dead,
        Position[TwoDimensionalSpace](1, 2) -> dead,
        Position[TwoDimensionalSpace](2, 0) -> dead,
        Position[TwoDimensionalSpace](2, 1) -> dead,
        Position[TwoDimensionalSpace](2, 2) -> dead,
      ).map(x => Cell(x._1, x._2))
    )

    val builder = DeclarativeRuleBuilder.configureRules:
      dead when fewerThan(3) withState(alive) whenCenterIs(alive)

    val rule = builder.build.head
    rule.applyTransformation(neighbourhood) shouldBe Cell(Position(1, 1), dead)
