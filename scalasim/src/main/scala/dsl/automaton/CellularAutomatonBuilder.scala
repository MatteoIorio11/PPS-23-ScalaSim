package dsl.automaton

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.automaton.CellularAutomaton.MultipleRulesCellularAutomaton
import domain.automaton.CellularAutomaton.State
import domain.automaton.Neighbour
import domain.automaton.NeighbourRule
import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace

import rule.NeighbourRuleBuilder
import rule.DeclarativeRuleBuilder
import rule.DeclarativeRuleBuilder.*

/**
  * A Generic [[CellularAutomaton]] builder for dive [[D]] dimensional space.
  */
trait CellularAutomatonBuilder[D <: Dimension]:
  /**
    * Adds a provided set of [[NeighbourRule]]s of a [[D]] dimensional space
    * to the rule collections of the generated [[CellularAutomaton]].
    *
    * @param rules
    * @return this builder.
    */
  def setRules(rules: Iterable[NeighbourRule[D]]): this.type
  
  /**
    * Build and retrieve the generated [[CellularAutomaton]]
    *
    * @return the built [[CellularAutomaton]]
    */
  def build(): CellularAutomaton[TwoDimensionalSpace]

/**
  * A [[CellularAutomatonBuilder]] companion object for two dimensional space.
  */
object CellularAutomatonBuilder:

  def apply(): CellularAutomatonBuilder[TwoDimensionalSpace] = CellularAutomatonBuilder2DImpl()

  /**
    * Generates a [[CellularAutomaton]] from a specified set of rules, in the form of
    * a [[NeighbourRuleBuilder]] block.
    *
    * @example
    * A CellularAutomaton can be built with a [[DeclarativeRuleBuilder]] in this way:
    * {{{
    * CellularAutomatonBuilder.fromRuleBuilder:
    *   DeclarativeRuleBuilder.configureRules:
    *     dead when fewerThan(2) withState alive whenCenterIs(alive)
    *     alive when surroundedBy(2) withState alive whenCenterIs(alive)
    *     alive whenNeighbourhoodIsExactlyLike:
    *       neighbour(alive) | c(dead) | neighbour(dead)
    * }}}
    * @param ruleBuilder the [[NeighbourRuleBuilder]] configuration block.
    * @return A [[CellularAutomatonBuilder]] of a two dimensional space.
    */
  def fromRuleBuilder
    (ruleBuilder: NeighbourRuleBuilder[TwoDimensionalSpace] ?=> NeighbourRuleBuilder[TwoDimensionalSpace]): CellularAutomatonBuilder[TwoDimensionalSpace] =
    given b: DeclarativeRuleBuilder = DeclarativeRuleBuilder()
    ruleBuilder
    val caBuilder = CellularAutomatonBuilder()
    caBuilder.setRules(ruleBuilder.build)
    caBuilder

  private class CellularAutomatonBuilder2DImpl() extends CellularAutomatonBuilder[TwoDimensionalSpace]:

    private val ca: CellularAutomaton[TwoDimensionalSpace] = MultipleRulesCellularAutomaton[TwoDimensionalSpace]()

    override def setRules(rules: Iterable[NeighbourRule[TwoDimensionalSpace]]): this.type =
      rules foreach ca.addRule
      this

    override def build(): CellularAutomaton[TwoDimensionalSpace] = ca