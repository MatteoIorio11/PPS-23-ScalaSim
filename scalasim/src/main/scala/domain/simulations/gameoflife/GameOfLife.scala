package domain.simulations.gameoflife

import domain.Environment
import domain.automaton.Cell
import domain.automaton.Cell.*
import domain.automaton.CellularAutomaton.*
import domain.automaton.NeighborRuleUtility
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.automaton.Neighbour
import domain.automaton.NeighbourRule
import domain.automaton.Rule
import domain.base.Dimensions.*
import domain.base.Position
import domain.simulations.gameoflife.GameOfLife.CellState
import domain.utils.ViewBag.ViewBag

import java.awt.Color
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

import Environment.*

/**
  * TODO: write how this CA work.
  */
trait GameOfLifeEnvironment extends SimpleEnvironment[TwoDimensionalSpace] with ArrayToroidEnvironment

object GameOfLifeEnvironment extends ViewBag:
    private val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), CellState.DEAD)

    def apply(height: Int, width: Int,  initialCells: Map[State, Int]): GameOfLifeEnvironment =
        GameOfLifeEnvironmentImpl(height, width, initialCells, cellularAutomata = GameOfLife())

    private class GameOfLifeEnvironmentImpl(val heigth: Int, val width: Int, val  initialCells: Map[State, Int], val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
        extends GameOfLifeEnvironment:

        require(heigth > 0, width > 0)
        require(cellularAutomata != null)
        require(initialCells.values.sum < heigth*width)

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().generalInitialization(dimension)(CellState.DEAD)

        initialise()
        override def neighbours(cell: Cell[TwoDimensionalSpace]): Neighbour[TwoDimensionalSpace] =
            import domain.automaton.NeighborRuleUtility.MooreNeighbourhood
            Neighbour[TwoDimensionalSpace](
                cell,
                availableCells(MooreNeighbourhood.absoluteNeighboursLocations(cell.position))
            )

        override protected def initialise(): Unit =
            initialCells.foreach((state, amount) => matrix.generalMultipleSpawn(dimension)(amount)(state))

    override def colors: Map[State, Color] = Map(
        GameOfLife.CellState.ALIVE -> Color.WHITE,
        GameOfLife.CellState.DEAD -> Color.BLACK
    )


object GameOfLife:
    import CellState.*
    import dsl.automaton.rule.DeclarativeRuleBuilder.*
    import dsl.automaton.rule.DeclarativeRuleBuilder.DSLExtensions.*
    import dsl.automaton.CellularAutomatonBuilder
    import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL.*
    import dsl.automaton.rule.DeclarativeRuleBuilder

    enum CellState extends State:
        case ALIVE
        case DEAD

    def apply(): CellularAutomaton[TwoDimensionalSpace] =
        CellularAutomatonBuilder.fromRuleBuilder {
            DeclarativeRuleBuilder.configureRules:
                DEAD when fewerThan(2) withState ALIVE whenCenterIs ALIVE
                ALIVE when surroundedBy(2) withState ALIVE whenCenterIs ALIVE
                ALIVE when surroundedBy(3) withState ALIVE whenCenterIs ALIVE
                DEAD when atLeastSurroundedBy(4) withState ALIVE whenCenterIs ALIVE
                ALIVE when surroundedBy(3) withState ALIVE whenCenterIs DEAD
        }.build()
