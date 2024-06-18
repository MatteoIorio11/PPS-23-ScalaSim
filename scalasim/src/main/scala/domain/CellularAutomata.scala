package domain

import domain.Dimensions.*
import domain.Neighbor
import domain.Rule
import domain.Cell.*
object CellularAutomata:
    trait State
    trait CellularAutomata[D <: Dimension, I, O]:
        type Rules
        def ruleCollection: Rules
        def dimension: D
        def applyRule(cell: Cell[D], neighbors: Neighbor[D]): Cell[D]
        def getNeighbours(cell: Cell[D]): List[Cell[D]]
        def getRules: Rules
        def addRule(cellState: State, rule: Rule[I, O]): Unit

