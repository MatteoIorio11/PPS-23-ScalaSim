package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Cell.*

object Environment:
    trait Environment[D <: Dimension, I, O]:
        type Matrix
        def cellularAutomata: CellularAutomata[D, I, O]
        def neighboors(cell: Cell[D]): List[Cell[D]]
        def start(): Unit
        def nextIteration(): Unit


object Environment2D:
    import Environment.*
    class GameOfLifeEnvironment(var cellularAutomata: CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]) extends Environment[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        override def neighboors(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
        override def start(): Unit = ???
        override def nextIteration(): Unit = ???
        type Matrix = Array[Array[Cell[TwoDimensionalSpace]]]
