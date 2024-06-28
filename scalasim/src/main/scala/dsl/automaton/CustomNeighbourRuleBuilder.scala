package dsl.automaton

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.CellularAutomaton.State
import domain.automaton.Neighbour
import domain.automaton.NeighbourRule
import scala.annotation.targetName

class CustomNeighbourRuleBuilder extends ExplicitNeighbourRuleBuilder:

  private var i: Int = 0
  private var j: Int = 0
  // TODO: change access modifiers in order to let tests pass
  var center: Option[Cell[TwoDimensionalSpace]] = Option.empty
  var cells: List[Cell[TwoDimensionalSpace]] = List.empty
  var rules: Set[NeighbourRule[TwoDimensionalSpace]] = Set.empty

  override def nextRow: this.type =
    i += 1
    j = 0
    this

  override def addCell(s: Option[State]): this.type =
    s match
      case Some(state) => cells = cells :+ Cell((i, j).toPosition, state)
      case _ =>
    j += 1
    this

  override def setCenter(s: State): this.type =
    center = Some(Cell((i, j).toPosition, s))
    j += 1
    this

  override def buildRule(s: State): Unit =
    import domain.automaton.NeighborRuleUtility.*

    rules = rules + ((n: Neighbour[TwoDimensionalSpace]) =>
        val neigbours = toAbsolutePosition(n.center).neighbourhood
        neigbours forall (n.neighbourhood.contains(_)) match
          case true => Cell(n.center.position, s)
          case _ => n.center
    )

  override def relativePositions: List[Cell[TwoDimensionalSpace]] =
    import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*

    center match
      case Some(c) => cells.map(p => Cell(p.position - c.position, p.state))
      case _ => throw IllegalStateException("Cannot compute relative positions if center is not defined")

  override def relativeNeighbourhood: Neighbour[TwoDimensionalSpace] =
    Neighbour(Cell((0, 0).toPosition, center.get.state), relativePositions)

  /**
   * Builds a [[Neighbour]] based on relative neighbourhood built with the DSL, using [[cntr]] as
   * a center and with respect to it, computing the absolute coordinates of all the other neighbours.
   *
   * @param cntr the center of the neighbourhood
   * @return a [[Neighbour]] with absolute coordinates based on [[cntr]] as center.
   */
  private def toAbsolutePosition(cntr: Cell[TwoDimensionalSpace]): Neighbour[TwoDimensionalSpace] =
    import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
    Neighbour(cntr, relativePositions.map(c => Cell(c.position + cntr.position, c.state)))

  /**
   * Resets building parameters for this builder, without altering rules list.
   */
  private def resetBuild: Unit =
    i = 0
    j = 0
    center = Option.empty
    cells = List.empty

  override def configureAnother(s: State)(config: ExplicitNeighbourRuleBuilder ?=> Unit): ExplicitNeighbourRuleBuilder =
    val otherBuilder = CustomNeighbourRuleBuilder()
    config(using otherBuilder)
    otherBuilder.buildRule(s)
    addRule(otherBuilder.rules.head)
    this

  override def addRule(nr: NeighbourRule[TwoDimensionalSpace]): Unit = rules = rules + nr

  extension (t: (Int, Int))
    private def toPosition: Position[TwoDimensionalSpace] = Position(t.toList)