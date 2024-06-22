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
import scala.collection.mutable.ArrayBuffer

object GameOfLifeEnvironment:
    var maxCellsToSpawn = 0
    val initialCell: Cell[TwoDimensionalSpace] = Cell(Position2D((-1, -1).toList), CellState.DEAD)

    def apply(dimension: Int): GameOfLifeEnvironmentImpl =
        maxCellsToSpawn = (dimension / 2) + 1
        GameOfLifeEnvironmentImpl(dimension, cellularAutomata = GameOfLife())

    extension (array: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]])
        def initializeEmpty2D(dimension: Int): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            val array = ArrayBuffer.fill(dimension, dimension)(initialCell)
            for (y <- 0 until dimension)
                for (x <- 0 until dimension)
                    array(x)(y) = Cell(Position2D((x, y).toList), CellState.DEAD)
            array

        def initialiseAliveCells(nCells: Int, dimension: Int): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            var spawnedCells = 0
            while (spawnedCells < nCells)
                val x = Random.nextInt(dimension)
                val y = Random.nextInt(dimension)
                val position = Position2D((x, y).toList)
                if (array(x)(y).state == CellState.DEAD)
                    array(x)(y) = Cell(position, CellState.ALIVE)
                    spawnedCells = spawnedCells + 1
            array

    import Environment.*
    class GameOfLifeEnvironmentImpl(
        val dimension: Int,
        val cellularAutomata: CellularAutomaton[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]],
        ) extends Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] 
            with ArrayEnvironment2D[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        require(dimension > 0)
        require(cellularAutomata != null)


        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] =
            positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < dimension))
              .map(pos => pos.coordinates.toList)
              .map(cor => matrix(cor.head)(cor.last))
        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeEmpty2D(dimension = dimension)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

        override protected def initialise(): Unit =
            val cells: Int = Random.nextInt(maxCellsToSpawn) + 1
            matrix = matrix.initialiseAliveCells(cells, dimension)
        
object GameOfLife:
    def apply(): CellularAutomaton[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] =
        val gameOfLife = GameOfLifeImpl()
        gameOfLife.addRule(CellState.ALIVE, (neighbours) => {
            val aliveNeighbours = neighbours.neighbourhood.count(_.state == CellState.ALIVE)
            Cell(neighbours.center.position, if (aliveNeighbours < 2 || aliveNeighbours > 3) CellState.DEAD else CellState.ALIVE)
        })
        gameOfLife
    
    enum CellState extends State:
        case ALIVE
        case DEAD
    private case class GameOfLifeImpl() extends CellularAutomaton[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        type Rules = Map[State, Rule[Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]]
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
                .map(rule => rule.applyTransformation(neighbours))
                .getOrElse(Cell(Position((0,0).toList), CellState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (cellState -> neighborRule)
