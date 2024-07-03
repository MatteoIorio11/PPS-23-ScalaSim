package dsl.automaton

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.automaton.CellularAutomaton.MutlipleRulesCellularAutomaton
import domain.automaton.Neighbour
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import dsl.automaton.CellularAutomatonBuilder.*
import dsl.automaton.rule.DeclarativeRuleBuilder
import dsl.automaton.rule.DeclarativeRuleBuilder.*
import dsl.automaton.rule.DeclarativeRuleBuilder.DSLExtensions.*
import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import utility.DummyAutomaton

class CellularAutomatonBuilderTest extends AnyFunSuite:
  private val alive = DummyAutomaton.DummyState.ALIVE
  private val dead  = DummyAutomaton.DummyState.DEAD

  test("`MultipleRuleCellularAutomaton` should behave as expected"):
    val rules = DeclarativeRuleBuilder.configureRules {
      dead when fewerThan(2) withState alive whenCenterIs(alive)
      alive when surroundedBy(2) withState alive whenCenterIs(alive)
    }.build

    val ca = MutlipleRulesCellularAutomaton[TwoDimensionalSpace]()
    rules foreach ca.addRule
    testCa(ca)

  test("`MultipleRuleCellularAutomaton` created with builder should behave as expected"):
      val caBuilder = CellularAutomatonBuilder.fromRuleBuilder:
        DeclarativeRuleBuilder.configureRules:
          dead when fewerThan(2) withState alive whenCenterIs(alive)
          alive when surroundedBy(2) withState alive whenCenterIs(alive)
          alive whenNeighbourhoodIsExactlyLike:
            neighbour(alive) | c(dead) | neighbour(dead)
      
      val ca = caBuilder.build()

      testCa(ca)

      val specificNeighbourhood = Neighbour[TwoDimensionalSpace](
        center = Cell(Position(0, 1), dead),
        neighbors = List(
          Cell(Position(0, 0), alive),
          Cell(Position(0, 2), dead)
        )
      )

      ca.applyRule(specificNeighbourhood).state shouldBe alive

  private def testCa(ca: CellularAutomaton[TwoDimensionalSpace]): Unit =
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
        Cell(Position(2, 1), dead),
        Cell(Position(0, 1), alive),
      )
    )

    ca.applyRule(aliveNeighbourhood).state shouldBe alive
    ca.applyRule(deadNeighbourhood).state shouldBe dead
