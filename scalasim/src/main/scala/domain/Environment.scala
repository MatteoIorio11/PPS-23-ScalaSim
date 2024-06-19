package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Cell.*
import scala.util.Random
import domain.Position.Position2D
import domain.GameOfLife.*

object Environment:
    trait Environment[D <: Dimension, I, O]:
        type Matrix
        def matrix: Matrix
        def dimension: Int
        def cellularAutomata: CellularAutomata[D, I, O]
        def neighboors(cell: Cell[D]): List[Cell[D]]
        protected def start(): Unit
        def nextIteration(): Unit


object GameOfLifeEnvironment:
    val maxCellsToSpawn = 50
    val initialCell = Cell(Position2D((-1, -1).toList), CellState.DEAD)

    def apply(dimension: Int): GameOfLifeEnvironmentImpl = 
        GameOfLifeEnvironmentImpl(dimension = dimension, cellularAutomata = GameOfLife())

    extension (array: Array[Array[Cell[TwoDimensionalSpace]]])
        def initializeEmpty2D(dimension: Int): Array[Array[Cell[TwoDimensionalSpace]]] =
            val array = Array.fill(dimension, dimension)(initialCell)
            for (y <- 0 until dimension)
                for (x <- 0 until dimension)
                    array(x)(y) = Cell(Position2D((x, y).toList), CellState.DEAD)
            array

        def initialiseAliveCells(nCells: Int, dimension: Int): Unit = 
            var usedPositions = Set[Position[TwoDimensionalSpace]]()
            for cell <- 0 until nCells
                x = Random.nextInt(dimension)
                y = Random.nextInt(dimension)
                position = Position2D((x, y).toList)
                if !usedPositions.contains(position)
            yield
                array(x)(y) = Cell(position, CellState.ALIVE)
                usedPositions = usedPositions.incl(position)

    import Environment.*
    class GameOfLifeEnvironmentImpl(val dimension: Int, val cellularAutomata: CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]) extends Environment[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        type Matrix = Array[Array[Cell[TwoDimensionalSpace]]]
        def matrix: Matrix = Array.ofDim[Array[Cell[TwoDimensionalSpace]]](dimension).initializeEmpty2D(dimension = dimension)
        override def neighboors(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = ???
        override def start(): Unit = 
            val cells: Int = Random.nextInt(maxCellsToSpawn) + 1
            matrix.initialiseAliveCells(cells, dimension)

        override def nextIteration(): Unit = ???
