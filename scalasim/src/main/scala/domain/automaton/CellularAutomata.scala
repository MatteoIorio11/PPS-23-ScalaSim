package domain.automaton

import domain.base.Dimensions.*
import domain.automaton.Neighbor
import domain.automaton.Rule
import domain.automaton.Cell.*
import domain.automaton.NeighborRuleUtility.NeighborhoodLocator
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.NeighborRule

object CellularAutomata:
    trait State
    trait CellularAutomata[D <: Dimension, I, O]:
        type Rules
        protected def ruleCollection: Rules
        def dimension: D
        def applyRule(cell: Cell[D], neighbors: Neighbor[D]): Cell[D]
        def neighboors(cell: Cell[D])(using locator: NeighborhoodLocator[D]): List[Position[D]] =
            locator.absoluteNeighborsLocations(cell.position).toList
        def rules: Rules
        def addRule(cellState: State, rule: NeighborRule[D]): Unit

