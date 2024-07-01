package dsl.automaton.rule

import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.NeighbourRule

/**
 * A generic [[NeighbourRule]] for [[D]] dimensional spaces.
 * @tparam D the dimensionality of the rules space.
 */
trait NeighbourRuleBuilder[D <: Dimension]:
  def rules: Set[NeighbourRule[D]]
  def addRule(nr: NeighbourRule[D]): Unit
  def build: Iterable[NeighbourRule[TwoDimensionalSpace]] // TODO: convert to lazylist
