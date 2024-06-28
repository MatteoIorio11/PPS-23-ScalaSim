package dsl.automaton

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.CellularAutomaton.State
import dsl.automaton.ExpressionRuleBuilder.*
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