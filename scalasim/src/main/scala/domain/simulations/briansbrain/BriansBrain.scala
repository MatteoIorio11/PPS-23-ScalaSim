package domain.simulations.briansbrain

import domain.Environment
import domain.automaton.Cell
import domain.automaton.Cell.*
import domain.automaton.CellularAutomaton.*
import domain.automaton.NeighborRuleUtility
import domain.base.Dimensions.*
import domain.base.Position
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.utils.ViewBag.ViewBag
import dsl.automaton.rule.DeclarativeRuleBuilder
import dsl.automaton.rule.DeclarativeRuleBuilder.ExpressionRuleDSL.surroundedBy

import java.awt.Color
import scala.collection.mutable.ArrayBuffer

import Environment.*
import domain.automaton.Neighbour

/**
  * TODO: write how this CA work.
  */
trait BriansBrainEnvironment extends SimpleEnvironment[TwoDimensionalSpace] with SquareArrayEnvironment2D

object BriansBrainEnvironment extends ViewBag:
  private val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), CellState.OFF)

  def apply(dimension: Int, initialCells: Map[? <: State, Int]): BriansBrainEnvironment =
    BriansBrainEnvironmentImpl(dimension, initialCells, cellularAutomata = BriansBrain())

  private class BriansBrainEnvironmentImpl(val side: Int, val initialCells: Map[? <:State, Int], val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
    extends BriansBrainEnvironment:

    require(side > 0)
    require(cellularAutomata != null)
    require(initialCells.values.sum < side*side)

    var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().generalInitialization(dimension)(CellState.OFF)

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
    CellState.ON -> Color.WHITE,
    CellState.OFF -> Color.BLACK,
    CellState.DYING -> Color.BLUE
  )

object BriansBrain:
  import dsl.automaton.CellularAutomatonBuilder
  import dsl.automaton.rule.DeclarativeRuleBuilder
  import dsl.automaton.rule.DeclarativeRuleBuilder.DSLExtensions.*
  import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL.*

  import CellState.*

  enum CellState extends State:
    case ON
    case OFF
    case DYING

  def apply(): CellularAutomaton[TwoDimensionalSpace] =
    CellularAutomatonBuilder.fromRuleBuilder {
      DeclarativeRuleBuilder.configureRules:
        DYING whenNeighbourhoodIsExactlyLike(c(ON))
        ON when surroundedBy(2) withState ON whenCenterIs OFF otherwise OFF
        OFF whenNeighbourhoodIsExactlyLike(c(DYING))
    }.build()
    