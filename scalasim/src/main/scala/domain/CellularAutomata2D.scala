package domain

import domain.CellularAutomata.*
import domain.Dimensions.*
import domain.Neighbor
import domain.Cell.*

object CellularAutomata2D:
    def apply(): CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] = 
        CellularAutomata2DImpl()
    
    enum CellularState extends State:
        case ALIVE
        case DEAD
    case class CellularAutomata2DImpl() extends CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        type Rules = Map[State, NeighborRule[TwoDimensionalSpace]]
        override val ruleCollection: Rules = Map()
        override val dimension: TwoDimensionalSpace = TwoDimensionalSpace()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbor[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] = ???
        override def getNeighbours(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
        override def getRules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: Rule[Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]): Unit = ???
