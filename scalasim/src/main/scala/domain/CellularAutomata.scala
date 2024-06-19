package domain

import domain.Dimensions.*
import domain.Neighbor
import domain.Rule
import domain.Cell.*
import domain.NeighborRuleUtility.NeighborhoodLocator
object CellularAutomata:
    trait State
    trait CellularAutomata[D <: Dimension, I, O]:
        type Rules
        protected def ruleCollection: Rules
        def dimension: D
        def applyRule(cell: Cell[D], neighbors: Neighbor[D]): Cell[D]
        def neighboors(cell: Cell[D])(using locator: NeighborhoodLocator[D]): List[Position[D]]
        def rules: Rules
        def addRule(cellState: State, rule: NeighborRule[D]): Unit

