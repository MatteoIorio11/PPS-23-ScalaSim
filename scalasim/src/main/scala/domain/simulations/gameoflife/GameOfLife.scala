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
    val initialCell: Cell[TwoDimensionalSpace] = Cell(Position2D((-1, -1).toList), CellState.DEAD)

    def apply(dimension: Int): GameOfLifeEnvironmentImpl =
        maxCellsToSpawn = (dimension / 2) + 1
        GameOfLifeEnvironmentImpl(dimension, cellularAutomata = GameOfLife())

    import Environment.*
    class GameOfLifeEnvironmentImpl(val side: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
        extends Environment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
        require(side > 0)
        require(cellularAutomata != null)

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(initialCell)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

        override protected def initialise(): Unit =
            val initialCell = Cell(Position((-1, -1).toList), CellState.DEAD)
            val array = ArrayBuffer.fill(side, side)(initialCell)
            for (y <- 0 until side)
                for (x <- 0 until side)
                    val probability = Random().nextBoolean()
                    val state = probability match
                        case x if x => CellState.ALIVE
                        case _ => CellState.DEAD
                    array(x)(y) = Cell(Position((x, y).toList), state)
            array(0)(0) = Cell(Position((0, 0).toList), CellState.DEAD)
            matrix = array.asInstanceOf[Matrix]

object GameOfLife:
    def apply(): CellularAutomaton[TwoDimensionalSpace] =
        val gameOfLife = GameOfLifeImpl()
        val liveRule: NeighbourRule[TwoDimensionalSpace] =  (x: Neighbour[TwoDimensionalSpace]) => {
            NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x).length match {
                case y if y < 2 || y > 3 => Cell(x.center.position.asPosition[Position2D], CellState.DEAD)
                case _ => Cell(x.center.position.asPosition[Position2D], CellState.ALIVE)
            }
        }
        gameOfLife.addRule(CellState.ALIVE, liveRule)

        val deadRule: NeighbourRule[TwoDimensionalSpace] =  (x: Neighbour[TwoDimensionalSpace]) => {
            NeighborRuleUtility.getNeighboursWithState(CellState.ALIVE, x).length match {
                case 3 => Cell(x.center.position.asPosition[Position2D], CellState.ALIVE)
                case _ => Cell(x.center.position.asPosition[Position2D], CellState.DEAD)
            }
        }
        gameOfLife.addRule(CellState.DEAD, deadRule)
        gameOfLife

    enum CellState extends State:
        case ALIVE
        case DEAD
    private case class GameOfLifeImpl() extends CellularAutomaton[TwoDimensionalSpace] with MapRules2D:
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
              .map(rule => rule.applyTransformation(neighbours))
              .getOrElse(Cell(Position((0,0).toList), CellState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (cellState -> neighborRule)