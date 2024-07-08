package domain.simulations.briansbrain

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
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.utils.ViewBag.ViewBag

import java.awt.Color
import scala.collection.mutable.ArrayBuffer

import Environment.*

trait BriansBrainEnvironment extends SimpleEnvironment[TwoDimensionalSpace] with SquareArrayEnvironment2D

object BriansBrainEnvironment extends ViewBag:
  private val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), CellState.OFF)

  def apply(dimension: Int): BriansBrainEnvironment =
    BriansBrainEnvironmentImpl(dimension, cellularAutomata = BriansBrain())

  private class BriansBrainEnvironmentImpl(val side: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
    extends BriansBrainEnvironment:

    require(side > 0)
    require(cellularAutomata != null)

    var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(CellState.OFF)

    initialise()

    override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
      import domain.automaton.NeighborRuleUtility.given
      availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

    override protected def initialise(): Unit =
      matrix.spawnCells(side*side/3)(CellState.ON)

  override def colors: Map[State, Color] = Map(
    CellState.ON -> Color.WHITE,
    CellState.OFF -> Color.BLACK,
    CellState.DYING -> Color.BLUE
  )

object BriansBrain:
  def apply(): CellularAutomaton[TwoDimensionalSpace] =
    val briansBrain = BriansBrainImpl()
    val onRule = NeighbourRule(Some(CellState.ON)): (x: Neighbour[TwoDimensionalSpace]) =>
      Cell(x.center.position, CellState.DYING)

    val offRule = NeighbourRule(Some(CellState.OFF)): (x: Neighbour[TwoDimensionalSpace]) =>
      NeighborRuleUtility.getNeighboursWithState(CellState.ON, x).length match
        case 2 => Cell(x.center.position, CellState.ON)
        case _ => Cell(x.center.position, CellState.OFF)

    val dyingRule = NeighbourRule(Some(CellState.DYING)): (x: Neighbour[TwoDimensionalSpace]) =>
      Cell(x.center.position, CellState.OFF)

    briansBrain.addRule(onRule)
    briansBrain.addRule(offRule)
    briansBrain.addRule(dyingRule)
    briansBrain

  enum CellState extends State:
    case ON
    case OFF
    case DYING

  private case class BriansBrainImpl() extends CellularAutomaton[TwoDimensionalSpace] with MapSingleRules[TwoDimensionalSpace]:
    var ruleCollection = Map()
    override def rules: Rules = ruleCollection

    override def addRule(neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
      neighborRule.matcher  match
        case Some(state) =>  ruleCollection = ruleCollection + (state -> neighborRule)
        case None => ruleCollection
