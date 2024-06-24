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
    /**
      * Trait that incapsulate the different state of the Cellular Automaton
      */
    trait State
    /**
     * Cellular automaton trait. It defines all the information about a general automaton.
     * A cellular automaton must define a collection of rules, where inside each rule is stored the logic
     * to apply to a particular cell.
     * @param D the [[Dimension]] of the space;
      */
    trait CellularAutomaton[D <: Dimension]:
        type Rules
        protected def ruleCollection: Rules
        def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D]
        def rules: Rules
        def addRule(cellState: State, rule: NeighbourRule[D]): Unit