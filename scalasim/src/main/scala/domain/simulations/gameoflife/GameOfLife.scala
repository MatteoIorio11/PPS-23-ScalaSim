package domain.simulations.gameoflife

import domain.Environment
import domain.Environment.Environment
import domain.automaton.CellularAutomaton.*
import domain.base.Dimensions.*
import domain.automaton.{Cell, NeighborRuleUtility, Neighbour, NeighbourRule, Rule}
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.Cell.*
import domain.base.Position
import domain.base.Position.Position2D
import domain.simulations.gameoflife.GameOfLife.CellState

import scala.util.Random
import scala.collection.mutable.ArrayBuffer

object GameOfLifeEnvironment:
    var maxCellsToSpawn = 0
    val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), CellState.DEAD)

    def apply(dimension: Int): GameOfLifeEnvironmentImpl =
        maxCellsToSpawn = (dimension / 2) + 1
        GameOfLifeEnvironmentImpl(dimension, cellularAutomata = GameOfLife())

    import Environment.*
    class GameOfLifeEnvironmentImpl(val dimension: Int,val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
      extends Environment[TwoDimensionalSpace]
      with ArrayEnvironment2D:
        require(dimension > 0)
        require(cellularAutomata != null)

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeEmpty2D(dimension = dimension)(initialCell)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

        override protected def initialise(): Unit =
            val initialCell = Cell(Position(-1, -1), CellState.DEAD)
            val array = ArrayBuffer.fill(dimension, dimension)(initialCell)
            for (y <- 0 until dimension)
                for (x <- 0 until dimension)
                    val probability = Random().nextBoolean()
                    val state = probability match
                        case x if x => CellState.ALIVE
                        case _ => CellState.DEAD
                    array(x)(y) = Cell(Position(x, y), state)
            array(0)(0) = Cell(Position(0, 0), CellState.DEAD)
            matrix = array.asInstanceOf[Matrix]

object GameOfLife:
    def apply(): CellularAutomaton[TwoDimensionalSpace] =
        val gameOfLife = GameOfLifeImpl()

        val liveRule = NeighbourRule(Some(CellState.ALIVE)): (x: Neighbour[TwoDimensionalSpace]) =>
            NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x).length match 
                case y if y < 2 || y > 3 => Cell(x.center.position, CellState.DEAD)
                case _ => Cell(x.center.position, CellState.ALIVE)
        
        val deadRule = NeighbourRule(Some(CellState.DEAD)): (x: Neighbour[TwoDimensionalSpace]) =>
            NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x).length match 
                case 3 => Cell(x.center.position, CellState.ALIVE)
                case _ => Cell(x.center.position, CellState.DEAD)

        gameOfLife.addRule(liveRule)
        gameOfLife.addRule(deadRule)
        gameOfLife

    enum CellState extends State:
        case ALIVE
        case DEAD

    private case class GameOfLifeImpl() extends CellularAutomaton[TwoDimensionalSpace]:
        type Rules = Map[State, NeighbourRule[TwoDimensionalSpace]]
        var ruleCollection: Rules = Map()

        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
              .map(rule => rule.applyTransformation(neighbours))
              .getOrElse(Cell(Position(0, 0), CellState.DEAD))

        override def rules: Rules = ruleCollection

        override def addRule(neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (neighborRule.matchingState.get -> neighborRule)
