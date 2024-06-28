package dsl.automaton

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.CellularAutomaton.State
import dsl.automaton.ExpressionRuleBuilder.*
import dsl.automaton.ExpressionRuleBuilder.DSLExtensions.*
import dsl.automaton.ExplicitNeighbourRuleBuilder.*
import domain.automaton.Cell
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour

class DeclarativeNeighbourRuleBuilderTest extends CustomNeighbourRuleBuilderTest:
  val dead = Dead()
  val alive = Alive()

  test("DSL syntax should work as expected"):
    ExpressionRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)
      alive when atLeastSurroundedBy(2) withState(alive)

  test("The builder should be capable of bulding a rule through the DSL"):
    val builder = ExpressionRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)

    builder.build()
    builder.rules.size shouldBe 1

  test("The DSL should map a correct rule"):
    val builder = ExpressionRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)

    builder.build()
    val aliveRule = builder.rules.head

    val aliveNeighbourhood = Neighbour[TwoDimensionalSpace](
        Cell((1, 0).toPostion, dead),
        List(
          Cell((0, 0).toPostion, alive),
          Cell((2, 0).toPostion, alive),
        ),
    )

    val aliveCell = Cell((1, 0).toPostion,  alive)
    aliveRule.applyTransformation(aliveNeighbourhood) shouldBe aliveCell

  test("The DSL shoudl map a correct rule even without optional parameters"):
    val builder = ExpressionRuleBuilder.configureRules:
      dead when atLeastSurroundedBy(1) withState(dead)

    builder.build()
    val rule = builder.rules.head

    val n = Neighbour[TwoDimensionalSpace](
        Cell((1, 0).toPostion, alive),
        List(
          Cell((0, 0).toPostion, alive),
          Cell((2, 0).toPostion, dead),
        ),
    )

    val expected = Cell((1, 0).toPostion, dead)
    rule.applyTransformation(n) shouldBe expected

  private object RulesTestUtils:
      val aliveNeighbourhood = Neighbour[TwoDimensionalSpace](
          Cell((1, 0).toPostion, dead),
          List(
            Cell((0, 0).toPostion, alive),
            Cell((2, 0).toPostion, alive),
          ),
      )

      val deadNeighbourhood = Neighbour[TwoDimensionalSpace](
          Cell((1, 1).toPostion, alive),
          List(
            (0, 0) -> dead,
            (2, 2) -> dead,
            (0, 2) -> dead,
            (2, 0) -> dead,
            (0, 1) -> alive,
            (1, 0) -> alive,
            (1, 2) -> alive,
            (2, 1) -> alive,
          ).map(x => Cell(x._1.toPostion, x._2))
      )

      val aliveCell = Cell((1, 0).toPostion, alive)
      val deadCell = Cell((1, 1).toPostion, dead)

  test("It should be possible to configure multiple rules in one configuration block"):
    val builder = ExpressionRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) whenCenterIs(dead)
      dead when surroundedBy(4) withState(dead)

    builder.build()
    
    builder.rules.size shouldBe 2

    val aliveRule = builder.rules.head
    val deadRule = builder.rules.last
    
    aliveRule.applyTransformation(RulesTestUtils.aliveNeighbourhood) shouldBe RulesTestUtils.aliveCell
    deadRule.applyTransformation(RulesTestUtils.deadNeighbourhood) shouldBe RulesTestUtils.deadCell
    
  test("It should be possible to specify an exact neighbour configuration as a rule"):
    val builder = ExpressionRuleBuilder.configureRules:
        dead whenNeighbourhoodIsExactlyLike:
          state(dead) | x        | state(dead) | n |
          x           | c(alive) | x           | n |
          state(dead) | x        | state(dead)

    val deadRule = builder.build().head
    deadRule.applyTransformation(RulesTestUtils.deadNeighbourhood) shouldBe RulesTestUtils.deadCell