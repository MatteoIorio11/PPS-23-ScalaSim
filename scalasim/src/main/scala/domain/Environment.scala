package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*

trait Environment[D <: Dimension]:
    // -- Attributes
    type Matrix
    def cellularAutomata: CellularAutomata

    // -- Methods
    def getNeighbours(cell: Cell[D]): List[Cell[D]]
    def start(): Unit
    def nextIteration(): Unit

