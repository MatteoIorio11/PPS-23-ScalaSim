package domain.simulations.briansbrain

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

object BriansBrainEnvironment:
  var maxCellsToSpawn = 0
  val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), CellState.DEAD)

  def apply(dimension: Int): BriansBrainEnvironmentImpl =
    maxCellsToSpawn = (dimension / 2) + 1
    BriansBrainEnvironmentImpl(dimension, cellularAutomata = BriansBrain())

  import Environment.*
  class BriansBrainEnvironmentImpl(val side: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
    extends Environment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
    require(side > 0)
    require(cellularAutomata != null)

    var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(initialCell)

    initialise()
    override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
      import domain.automaton.NeighborRuleUtility.given
      availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

    override protected def initialise(): Unit =
      val initialCell = Cell(Position(-1, -1), CellState.DEAD)
      matrix.spawnCells(side*side/3)(CellState.ALIVE)

object BriansBrain:
  def apply(): CellularAutomaton[TwoDimensionalSpace] =
    val briansBrain = BriansBrainImpl()
    briansBrain

  enum CellState extends State:
    case ON
    case OFF
    case DYING

  private case class BriansBrainImpl() extends CellularAutomaton[TwoDimensionalSpace] with MapRules2D:
    var ruleCollection: Rules = Map()

    override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
      ruleCollection.get(cell.state)
        .map(rule => rule.applyTransformation(neighbours))
        .getOrElse(Cell(Position(0, 0), CellState.OFF))

    override def rules: Rules = ruleCollection

    override def addRule(neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
      ruleCollection = ruleCollection + (neighborRule.matchingState.get -> neighborRule)


