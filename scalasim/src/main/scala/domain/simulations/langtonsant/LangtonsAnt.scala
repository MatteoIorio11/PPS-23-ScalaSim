package domain.simulations.langtonsant

import domain.utils.ViewBag.ViewBag
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.Environment.Environment
import domain.Environment.SquareArrayEnvironment2D
import java.awt.Color
import domain.automaton.CellularAutomaton.State
import domain.automaton.Cell
import scala.collection.mutable
import domain.automaton.CellularAutomaton.ComplexCellularAutomaton
import domain.automaton.MultipleOutputNeighbourRule
import domain.automaton.Neighbour
import domain.base.Position
import domain.automaton.NeighborRuleUtility.RelativePositions
import domain.automaton.NeighborRuleUtility.circleNeighbourhoodLocator
import domain.base.Dimensions.Dimension
import scala.collection.mutable.ArrayBuffer
import domain.automaton.CellularAutomaton.AnyState

object LangtonsAntEnvironment extends ViewBag:

  override def colors: Map[State, Color] = Map()

  class LangtonsAntEnvironmentImpl(val side: Int, val ca: CellularAutomaton[TwoDimensionalSpace])
    extends Environment[TwoDimensionalSpace] with SquareArrayEnvironment2D:

    var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]()
    
    initialise()

    override def cellularAutomata: CellularAutomaton[TwoDimensionalSpace] = ???

    override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
      availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

    override protected def initialise(): Unit =
      import LangtonsAntAutomaton.LangstonAntStates.*
      matrix = matrix.spawnCell(WHITE)(WHITE)
      matrix(side/2)(side/2) = Cell(Position[TwoDimensionalSpace](side/2, side/2), ANT(WHITE))

object LangtonsAntAutomaton:
  import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
  import domain.automaton.NeighborRuleUtility.RelativePositions.*
  import LangstonAntStates.*

  enum LangstonAntStates extends State:
    case WHITE
    case BLACK
    case ANT(cellColor: State = AnyState)

    def invert: LangstonAntStates = this match
      case ANT(x) => ANT(x)
      case c => if c == WHITE then BLACK else WHITE

  def apply(): ComplexCellularAutomaton[TwoDimensionalSpace] =
    val ca = LangtonsAntAutomatonImpl()

    val antRule = MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(ANT())): n =>
      val updates = n.center.state.asInstanceOf[ANT].cellColor.asInstanceOf[LangstonAntStates] match
        case WHITE => (BLACK, East)
        case BLACK => (WHITE, West)
        case _ => throw IllegalStateException("Ant cannot have another ant under it")

      val newPosition = n.center.position.moveTo(updates._2)
      val newPositionState = n.neighbourhood.find(_.position == newPosition).get.state

      Iterable(
        Cell(n.center.position, updates._1),
        Cell(newPosition, newPositionState)
      )

    Iterable(
      antRule,
      MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(WHITE))(n => List.empty),
      MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(BLACK))(n => List.empty),
    ) foreach ca.addRule

    ca
  
  extension[D <: Dimension] (p: Position[D])
    private def moveTo(rp: RelativePositions): Position[D] = p + rp

  private class LangtonsAntAutomatonImpl extends ComplexCellularAutomaton[TwoDimensionalSpace]:
    private var rules: Map[State, MultipleOutputNeighbourRule[TwoDimensionalSpace]] = Map()

    override def addRule(rule: MultipleOutputNeighbourRule[TwoDimensionalSpace]): Unit =
      rules = rules + (rule.matcher.get -> rule)

    override def applyRule(neighbors: Neighbour[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
      rules.get(neighbors.center.state) match
        case None => Iterable(neighbors.center)
        case Some(r) => r.applyTransformation(neighbors)
      
