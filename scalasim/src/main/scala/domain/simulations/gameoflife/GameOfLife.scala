package domain.simulations.gameoflife

import domain.Environment
import domain.Environment.Environment
import domain.automaton.CellularAutomaton.*
import domain.base.Dimensions.*
import domain.automaton.Neighbour
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.Cell.*
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.{NeighbourRule, Rule}
import domain.base.Position.Position2D
import domain.simulations.gameoflife.GameOfLife.CellState

import scala.util.Random

object GameOfLifeEnvironment:
    val maxCellsToSpawn = 50
    val initialCell: Cell[TwoDimensionalSpace] = Cell(Position2D((-1, -1).toList), CellState.DEAD)

    def apply(dimension: Int): GameOfLifeEnvironmentImpl =
        GameOfLifeEnvironmentImpl(dimension, cellularAutomata = GameOfLife())

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
        val cellularAutomata: CellularAutomata[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]],
        ) extends Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] 
            with ArrayEnvironment2D[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        require(dimension > 0)
        require(cellularAutomata != null)


        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] =
            positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < dimension))
              .map(pos => pos.coordinates.toList)
              .map(cor => matrix(cor.head)(cor.last))
        override def matrix: Matrix = Array.ofDim[Array[Cell[TwoDimensionalSpace]]](dimension).initializeEmpty2D(dimension = dimension)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

        override protected def initialise(): Unit =
            val cells: Int = Random.nextInt(maxCellsToSpawn) + 1
            matrix.initialiseAliveCells(cells, dimension)


object GameOfLife:
    def apply(): CellularAutomata[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] =
        GameOfLifeImpl()
    
    enum CellState extends State:
        case ALIVE
        case DEAD
    private case class GameOfLifeImpl() extends CellularAutomata[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        type Rules = Map[State, Rule[Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]]
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
                .map(rule => rule.applyTransformation(neighbours))
                .getOrElse(Cell(Position((0,0).toList), CellState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (cellState -> neighborRule)
