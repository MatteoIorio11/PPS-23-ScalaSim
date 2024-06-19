package domain

import domain.CellularAutomata.*
import domain.Dimensions.*
import domain.Neighbor
import domain.Cell.*

object GameOfLife:
    def apply(): CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] = 
        GameOfLifeImpl()
    
    enum CellState extends State:
        case ALIVE
        case DEAD
    case class GameOfLifeImpl() extends CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        type Rules = Map[State, Rule[Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]]
        var ruleCollection: Rules = Map()
        override val dimension: TwoDimensionalSpace = TwoDimensionalSpace()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbor[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] = ???
        override def neighboors(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighborRule[TwoDimensionalSpace]): Unit = 
            ruleCollection = ruleCollection.+(kv=(cellState, neighborRule))