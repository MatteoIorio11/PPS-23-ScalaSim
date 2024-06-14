package domain

import domain.Dimensions.*
import domain.Neighbor.*
import domain.Cell.*
object CellularAutomata:
    trait State extends Enumeration
    trait CellularAutomata[D <: Dimension]:
        type Rules
        def ruleCollection: Rules
        def dimension: D
        def applyRule(cell: Cell[D], neighbours: List[Cell[D]]): Cell[D]
        def getNeighbours(cell: Cell[D]): List[Cell[D]]
        def getRules: Rules
        def addRule(cellState: State, neighborRule: NeighborRule[D]): Unit

    object CellularAutomata2D:
        enum CellularState extends State:
            case ALIVE
            case DEAD
        case class CellularAutomata2D() extends CellularAutomata[TwoDimensionalSpace]:
           type Rules = Map[State, NeighborRule[TwoDimensionalSpace]]
            override val ruleCollection: Rules = Map()
            override val dimension: TwoDimensionalSpace = TwoDimensionalSpace()
            override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: List[Cell[TwoDimensionalSpace]]): Cell[TwoDimensionalSpace] = ???
            override def getNeighbours(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
            override def getRules: Rules = ruleCollection
            override def addRule(cellState: State, neighborRule: NeighborRule[TwoDimensionalSpace]): Unit = ???
