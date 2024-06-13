
import domain.Dimensions.*
trait CellularAutomata[D: Dimension]
    type Rules
    def dimension: D

    def applyRule(cell: Cell[D], neighbours: List[Cell[D]]): Cell
    def getNeighbours(cell: Cell[D]): List[Cell[D]]