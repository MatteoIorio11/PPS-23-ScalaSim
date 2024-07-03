package domain.simulations.gameoflife

import domain.Environment
import domain.Environment.Environment
import domain.automaton.CellularAutomaton.*
import domain.base.Dimensions.*
import domain.automaton.{Cell, NeighborRuleUtility, Neighbour, NeighbourRule, Rule}
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.Cell.*
import domain.base.Position
import domain.simulations.gameoflife.GameOfLife.CellState

import scala.util.Random
import scala.collection.mutable.ArrayBuffer

object GameOfLifeEnvironment:
    var maxCellsToSpawn = 0
    val initialState = CellState.DEAD

    def apply(dimension: Int): GameOfLifeEnvironmentImpl =
        maxCellsToSpawn = (dimension / 2) + 1
        GameOfLifeEnvironmentImpl(dimension, cellularAutomata = GameOfLife())

    import Environment.*
    class GameOfLifeEnvironmentImpl(val side: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
      extends Environment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
        require(side > 0)
        require(cellularAutomata != null)

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(initialState)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

        override protected def initialise(): Unit =
            matrix = matrix.spawnCell(CellState.DEAD)(CellState.ALIVE)
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
    private case class GameOfLifeImpl() extends CellularAutomaton[TwoDimensionalSpace] with MapSingleRules[TwoDimensionalSpace]:
        var ruleCollection: Rules = Map()

        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
              .map(rule => rule.applyTransformation(neighbours))
              .getOrElse(Cell(Position(0, 0), CellState.DEAD))

        override def rules: Rules = ruleCollection

        override def addRule(neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (neighborRule.matcher.get -> neighborRule)