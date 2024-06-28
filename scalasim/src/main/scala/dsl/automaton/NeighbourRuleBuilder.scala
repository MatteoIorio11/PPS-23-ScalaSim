package dsl.automaton

import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.NeighbourRule

trait NeighbourRuleBuilder[D <: Dimension]:
  def rules: Set[NeighbourRule[D]]
  def addRule(nr: NeighbourRule[D]): Unit
  def build: Iterable[NeighbourRule[TwoDimensionalSpace]] // TODO: convert to lazylist
