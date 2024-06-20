package domain

import scala.collection.mutable.ArrayBuffer
import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Cell.*
import scala.util.Random
import domain.Position.Position2D
import domain.GameOfLife.*
import domain.NeighborRuleUtility.NeighborhoodLocator

object Environment:
    trait Environment[D <: Dimension, I, O]:
        type Matrix
        def matrix: Matrix
        def dimension: Int
        def cellularAutomata: CellularAutomata[D, I, O]
        def neighboors(cell: Cell[D]): List[Cell[D]]
        protected def initialise(): Unit
        protected def availableCells(positions: List[Position[D]]): List[Cell[D]]
    

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
            var spawnedCells = 0
            while (spawnedCells < nCells)
                val x = Random.nextInt(dimension)
                val y = Random.nextInt(dimension)
                val position = Position2D((x, y).toList)
                if (array(x)(y).state == CellState.DEAD)
                    array(x)(y) = Cell(position, CellState.ALIVE)
                    spawnedCells = spawnedCells + 1

    import Environment.*
    class GameOfLifeEnvironmentImpl(
        val dimension: Int,
        val cellularAutomata: CellularAutomata[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]],
        ) extends Environment[TwoDimensionalSpace, Neighbor[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        require(dimension > 0)
        require(cellularAutomata != null)


        override protected def availableCells(positions: List[Position[TwoDimensionalSpace]]): List[Cell[TwoDimensionalSpace]] = 
            positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < dimension))
                .map(pos => pos.coordinates.toList)
                .map(cor => matrix(cor(0))(cor(1)))

        type Matrix = Array[Array[Cell[TwoDimensionalSpace]]]
        var matrix: Matrix = Array.ofDim[Array[Cell[TwoDimensionalSpace]]](dimension).initializeEmpty2D(dimension = dimension)
        
        initialise()
        override def neighboors(cell: Cell[TwoDimensionalSpace]): List[Cell[TwoDimensionalSpace]] = 
            import domain.NeighborRuleUtility.given
            availableCells(circleNeighborhoodLocator.absoluteNeighborsLocations(cell.position).toList)

        override protected def initialise(): Unit = 
            val cells: Int = Random.nextInt(maxCellsToSpawn) + 1
            matrix.initialiseAliveCells(cells, dimension)