package domain

import domain.Dimensions.*
import domain.Neighbor
import domain.Rule
import domain.Cell.*
object CellularAutomata:
    trait State
    trait CellularAutomata[D <: Dimension]:
        type Rules
        def ruleCollection: Rules
        def dimension: D
        def applyRule(cell: Cell[D], neighbours: List[Cell[D]]): Cell[D]
        def getNeighbours(cell: Cell[D]): List[Cell[D]]
        def getRules: Rules
        // TODO CAMBIARE SOTTO
        def addRule(cellState: State, neighborRule: NeighborRule[D]): Unit

