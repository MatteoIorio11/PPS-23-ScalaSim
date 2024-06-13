
import domain.Dimensions.*
import domain.NeighborRule.*
import java.awt.Dimension

trait CellularAutomata[D: Dimension]
    type Rules
    def ruleCollection: Rules
    def dimension: D

    def applyRule(cell: Cell[D], neighbours: List[Cell[D]]): Cell[D]
    def getNeighbours(cell: Cell[D]): List[Cell[D]]


class CellularAutomata2D[TwoDimensionalSpace](private val dimension: TwoDimensionalSpace) extends CellularAutomata[TwoDimensionalSpace]:
    override type Rules = Map[CellState.State, NeighborRule]
    val ruleCollection: Rules = Map()

    def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: List[Cell[TwoDimensionalSpace]]): Cell[TwoDimensionalSpace] = ???
    def getNeighbours(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
    def getRules(): Rules = ???
