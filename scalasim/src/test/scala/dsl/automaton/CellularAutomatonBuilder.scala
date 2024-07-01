package dsl.automaton

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.CellularAutomaton.State
import dsl.automaton.DeclarativeRuleBuilder.*
import dsl.automaton.DeclarativeRuleBuilder.DSLExtensions.*
import dsl.automaton.CellularAutomatonBuilder.*
import domain.automaton.Neighbour
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position

class CellularAutomatonBuilderTest extends AnyFunSuite:
  val alive = new State {}
  val dead  = new State {}

  test("`MultipleRuleCellularAutomaton` should behave as expected"):
    val rules = DeclarativeRuleBuilder.configureRules {
      dead when fewerThan(2) withState alive whenCenterIs(alive)
      dead when surroundedBy(3) withState alive whenCenterIs(alive)
    }.build

    val ca = MultipleRuleCellularAutomaton2D(List(alive, alive) zip rules)

    val center = Cell[TwoDimensionalSpace](Position(0, 1), alive)

    val firstRuleNeigh: Neighbour[TwoDimensionalSpace] = Neighbour(
      center,
      List(
        Cell(Position(0, 0), dead),
        Cell(Position(0, 2), alive),
      )
    )

    val secondRuleNeigh: Neighbour[TwoDimensionalSpace] = Neighbour(
      center,
      List(
        Cell(Position(0, 0), alive),
        Cell(Position(0, 2), alive),
        Cell(Position(1, 1), alive),
      )
    )

    val noMatchingNeigh: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(center.position, dead),
      secondRuleNeigh.neighbourhood,
    )

    val deadCenter = Cell[TwoDimensionalSpace](Position(0, 1), dead)

    ca.applyRule(center, firstRuleNeigh).state shouldBe dead
    ca.applyRule(center, secondRuleNeigh).state shouldBe dead
    ca.applyRule(deadCenter, noMatchingNeigh) shouldBe deadCenter
    ca.applyRule(deadCenter, firstRuleNeigh) shouldBe deadCenter
    ca.applyRule(deadCenter, secondRuleNeigh) shouldBe deadCenter