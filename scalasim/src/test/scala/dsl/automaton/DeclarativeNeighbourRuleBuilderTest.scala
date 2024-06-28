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

  test("The builder should be capable of bulding rules through the DSL"):
    val builder = ExpressionRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)

    builder.build()
    builder.rules.size shouldBe 1

  test("The DSL should map a correct rule"):
    val builder = ExpressionRuleBuilder.configureRules:
      alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)
      // alive when atLeastSurroundedBy(2) withState(alive) <----- problems

    val rule = builder.build().head

    val neighbourhood = Neighbour[TwoDimensionalSpace](
        Cell((1, 0).toPostion, dead),
        List(
          Cell((0, 0).toPostion, alive),
          Cell((2, 0).toPostion, alive),
        ),
    )

    val expectedCell = Cell((1, 0).toPostion,  alive)
    rule.applyTransformation(neighbourhood) shouldBe expectedCell