package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Cell.*

object Environment:
    trait Environment[D <: Dimension, I, O]:
        type Matrix
        def matrix: Matrix
        def dimension: Int
        def cellularAutomata: CellularAutomata[D, I, O]
        def neighboors(cell: Cell[D]): List[Cell[D]]
        def start(): Unit
        def nextIteration(): Unit


object GameOfLifeEnvironment:
    def apply(dimension: Int): GameOfLifeEnvironmentImpl = 
        GameOfLifeEnvironmentImpl(dimension = dimension, cellularAutomata = GameOfLife())
    import Environment.*
    class GameOfLifeEnvironmentImpl(val dimension: Int, val cellularAutomata: CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]) extends Environment[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        override def matrix: Matrix = ???
        override def neighboors(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
        override def start(): Unit = ???
        override def nextIteration(): Unit = ???
        type Matrix = Array[Array[Cell[TwoDimensionalSpace]]]
