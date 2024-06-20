package domain.automaton

import domain.base.Dimensions.*
import domain.automaton.Neighbour
import domain.automaton.Rule
import domain.automaton.Cell.*
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.NeighbourRule

object CellularAutomaton:
    trait State
    trait CellularAutomata[D <: Dimension, I, O]:
        type Rules
        protected def ruleCollection: Rules
        def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D]
        def rules: Rules
        def addRule(cellState: State, rule: NeighbourRule[D]): Unit

