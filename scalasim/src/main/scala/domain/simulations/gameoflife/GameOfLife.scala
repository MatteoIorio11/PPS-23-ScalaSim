package domain.simulations.gameoflife

import domain.Environment
import domain.automaton.CellularAutomaton.*
import domain.base.Dimensions.*
import domain.automaton.{Cell, NeighborRuleUtility, Neighbour, NeighbourRule, Rule}
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.Cell.*
import domain.base.Position
import domain.simulations.gameoflife.GameOfLife.CellState
import domain.utils.ViewBag.ViewBag

import java.awt.Color
import scala.util.Random
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

import Environment.*

/**
  * TODO: write how this CA work.
  */
trait GameOfLifeEnvironment extends SimpleEnvironment[TwoDimensionalSpace] with ArrayToroidEnvironment

object GameOfLifeEnvironment extends ViewBag:
    private val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), CellState.DEAD)

    def apply(height: Int, width: Int): GameOfLifeEnvironment =
        GameOfLifeEnvironmentImpl(height, width, cellularAutomata = GameOfLife())

    private class GameOfLifeEnvironmentImpl(val heigth: Int, val width: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
        extends GameOfLifeEnvironment:

        require(heigth > 0, width > 0)
        require(cellularAutomata != null)

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(CellState.DEAD)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

        override protected def initialise(): Unit =
            val initialCell = Cell(Position(-1, -1), CellState.DEAD)
            matrix.spawnCells(heigth*width/3)(CellState.ALIVE)

    override def colors: Map[State, Color] = Map(
        GameOfLife.CellState.ALIVE -> Color.WHITE,
        GameOfLife.CellState.DEAD -> Color.BLACK
    )


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
        override def rules: Rules = ruleCollection
        override def addRule(neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            neighborRule.matcher  match
                case Some(state) =>  ruleCollection = ruleCollection + (state -> neighborRule)
                case None => ruleCollection

