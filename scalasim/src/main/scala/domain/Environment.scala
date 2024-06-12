package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*

trait Environment[D <: Dimension]:
    // -- Attributes
    type Matrix = Array[Array[Cell[D]]]
    def cellularAutomata: CellularAutomata

    // -- Methods
    def getNeighbours(cell: Cell[D]): List[Cell[D]]
    def start(): Unit
    def nextIteration(): Unit