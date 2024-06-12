package domain

import scala.collection.mutable.ArrayBuffer

trait Environment:
    // -- Attributes
    type Matrix = Array[Array[Cell[D <: Dimension]]]
    def cellularAutomata: CellularAutomata

    // -- Methods
    def getNeighbours(cell: Cell[D <: Dimension]): List[Cell[D <: Dimension]]
    def start(): Unit
    def nextIteration(): Unit