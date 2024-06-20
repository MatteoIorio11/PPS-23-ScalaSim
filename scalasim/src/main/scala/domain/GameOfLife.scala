package domain

import domain.CellularAutomata.*
import domain.Dimensions.*
import domain.Neighbor
import domain.NeighborRuleUtility.NeighborhoodLocator
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
        override def neighboors(cell: Cell[TwoDimensionalSpace])(using locator: NeighborhoodLocator[TwoDimensionalSpace]): List[Position[TwoDimensionalSpace]] = ???
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighborRule[TwoDimensionalSpace]): Unit = 
            ruleCollection = ruleCollection + (cellState -> neighborRule)
