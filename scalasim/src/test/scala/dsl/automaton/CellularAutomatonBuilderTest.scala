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
import domain.automaton.CellularAutomaton.CellularAutomaton

class CellularAutomatonBuilderTest extends AnyFunSuite:
  val alive = new State {}
  val dead  = new State {}

  test("`MultipleRuleCellularAutomaton` should behave as expected"):
    val rules = DeclarativeRuleBuilder.configureRules {
      dead when fewerThan(2) withState alive whenCenterIs(alive)
      alive when surroundedBy(2) withState alive whenCenterIs(alive)
    }.build

    val center = Cell[TwoDimensionalSpace](Position(1, 1), alive)

    val aliveNeighbourhood = Neighbour[TwoDimensionalSpace](
      center,
      List(
        Cell(Position(0, 1), alive),
        Cell(Position(2, 1), alive),
      )
    )

    val deadNeighbourhood = Neighbour[TwoDimensionalSpace](
      center,
      List(
        Cell(Position(0, 1), alive),
        Cell(Position(2, 1), dead),
      )
    )

    val ca = MultipleRuleCellularAutomaton2D(List(alive, alive) zip rules)

    ca.applyRule(center, aliveNeighbourhood).state shouldBe alive
    ca.applyRule(center, deadNeighbourhood).state shouldBe dead

  // test("`MultipleRuleCellularAutomaton` created with builder should behave as expected"):
  //     val ca = CellularAutomatonBuilder.fromRuleBuilder {
  //       DeclarativeRuleBuilder.configureRules:
  //         dead when fewerThan(2) withState alive whenCenterIs(alive)
  //         dead when surroundedBy(3) withState alive whenCenterIs(alive)
  //     }.build()