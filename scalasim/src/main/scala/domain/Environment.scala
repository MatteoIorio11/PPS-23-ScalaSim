package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Cell.*

object Environment:
    trait Environment[D <: Dimension]:
        type Matrix
        def cellularAutomata: CellularAutomata[D]
        def getNeighbours(cell: Cell[D]): List[Cell[D]]
        def start(): Unit
        def nextIteration(): Unit
