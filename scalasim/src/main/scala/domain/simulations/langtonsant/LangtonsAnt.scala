package domain.simulations.langtonsant

import domain.Environment.SquareArrayEnvironment2D
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.AnyState
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.automaton.CellularAutomaton.ComplexCellularAutomaton
import domain.automaton.CellularAutomaton.State
import domain.automaton.MultipleOutputNeighbourRule
import domain.automaton.NeighborRuleUtility.RelativePositions
import domain.automaton.NeighborRuleUtility.circleNeighbourhoodLocator
import domain.automaton.Neighbour
import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.utils.ViewBag.ViewBag

import java.awt.Color
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import domain.Environment.ComplexEnvironment

object LangtonsAntEnvironment extends ViewBag:
  import LangtonsAntAutomaton.CellState.*
  import LangtonsAntAutomaton.LangstonAntState.ANT

  def apply(dimension: Int): LangtonsAntEnvironmentImpl =
    LangtonsAntEnvironmentImpl(dimension, LangtonsAntAutomaton())
  override def colors: Map[State, Color] = 
    Map(
      ANT(WHITE) -> Color.LIGHT_GRAY,
      ANT(BLACK) -> Color.DARK_GRAY,
      WHITE -> Color.WHITE,
      BLACK -> Color.BLACK,
    )

  class LangtonsAntEnvironmentImpl(val side: Int, val ca: ComplexCellularAutomaton[TwoDimensionalSpace])
    extends ComplexEnvironment[TwoDimensionalSpace] with SquareArrayEnvironment2D:

    var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]()
    
    initialise()

    override def cellularAutomata: ComplexCellularAutomaton[TwoDimensionalSpace] = LangtonsAntAutomaton()

    override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
      availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

    override protected def initialise(): Unit =
      matrix = matrix.spawnCell(WHITE)(WHITE)
      matrix(side/2)(side/2) = Cell(Position[TwoDimensionalSpace](side/2, side/2), ANT(WHITE))

trait LangtonsAntAutomaton extends ComplexCellularAutomaton[TwoDimensionalSpace]

object LangtonsAntAutomaton:
  import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
  import domain.automaton.NeighborRuleUtility.RelativePositions.*
  import CellState.*
  import LangstonAntState.ANT

  enum CellState extends State:
    case WHITE
    case BLACK
    def invert: CellState = if this == WHITE then BLACK else WHITE

  enum LangstonAntState extends State:
    case ANT(cellColor: CellState, direction: RelativePositions = South)

    override def equals(x: Any): Boolean =
      this.isInstanceOf[ANT] && x.isInstanceOf[ANT] && this.asInstanceOf[ANT].cellColor == x.asInstanceOf[ANT].cellColor

  def apply(): LangtonsAntAutomaton =
    def antRule(n: Neighbour[TwoDimensionalSpace], moveCenterTo: RelativePositions): Iterable[Cell[TwoDimensionalSpace]] =
      val oldPositionState = n.center.state.asInstanceOf[ANT].cellColor.invert

      val direction = oldPositionState.invert match
        case WHITE => n.center.state.asInstanceOf[ANT].direction.clockWiseRotate
        case BLACK => n.center.state.asInstanceOf[ANT].direction.counterClockWiseRotate
      
      val newPosition = n.center.position.moveTo(direction)
      val newPositionState = n.neighbourhood.find(_.position == newPosition).get.state.asInstanceOf[CellState]


      Iterable(
        Cell(n.center.position, oldPositionState),
        Cell(newPosition, ANT(newPositionState, direction))
      )

    val ca = LangtonsAntAutomatonImpl()

    Iterable(
      MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(ANT(WHITE)))(n => antRule(n, East)),
      MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(ANT(BLACK)))(n => antRule(n, West)),
      MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(WHITE))(n => List.empty),
      MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(BLACK))(n => List.empty),
    ) foreach ca.addRule

    ca
  
  extension[D <: Dimension] (p: Position[D])
    private def moveTo(rp: RelativePositions): Position[D] = p + rp

  extension (rp: RelativePositions)
    private def clockWiseRotate: RelativePositions = rp match
      case North => East
      case East => South
      case South => West
      case West => North
      case _ => rp

    private def counterClockWiseRotate: RelativePositions = rp match
      case North => West
      case West => South
      case South => East
      case East => North
      case _ => rp

  private class LangtonsAntAutomatonImpl extends LangtonsAntAutomaton:
    private var rules: Map[State, MultipleOutputNeighbourRule[TwoDimensionalSpace]] = Map()

    override def addRule(rule: MultipleOutputNeighbourRule[TwoDimensionalSpace]): Unit =
      rules = rules + (rule.matcher.get -> rule)

    override def applyRule(neighbors: Neighbour[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
      rules.get(neighbors.center.state) match
        case None => Iterable(neighbors.center)
        case Some(r) => r.applyTransformation(neighbors)
      
