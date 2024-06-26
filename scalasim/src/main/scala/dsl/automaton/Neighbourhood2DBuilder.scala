package dsl.automaton

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.CellularAutomaton.State
import domain.automaton.Neighbour
import domain.automaton.NeighbourRule

class Neighbourhood2DBuilder:

  var i: Int = 0
  var j: Int = 0
  var center: Option[Cell[TwoDimensionalSpace]] = Option.empty
  var cells: List[Cell[TwoDimensionalSpace]] = List.empty
  var rules: List[NeighbourRule[TwoDimensionalSpace]] = List.empty

  def nextRow: this.type =
    i += 1
    j = 0
    this

  def addCell(s: Option[State]): this.type =
    s match
      case Some(state) => cells = cells :+ Cell((i, j).toPosition, state)
      case _ =>
    j += 1
    this

  def setCenter(s: State): this.type =
    center = Some(Cell((i, j).toPosition, s))
    j += 1
    this
    
  private def buildRule(s: State): Unit =
    import domain.automaton.NeighborRuleUtility.*

    rules = rules :+ ((n: Neighbour[TwoDimensionalSpace]) =>
        val currentRuleToAdjustedPositions = toAbsolutePosition(n.center)
        if n == currentRuleToAdjustedPositions
          then Cell(n.center.position, s)
          else n.center
        )

  def relativePositions: List[Cell[TwoDimensionalSpace]] =
    import domain.automaton.NeighborRuleUtility.-

    center match
      case Some(c) => cells.map: p =>
        Cell(p.position - c.position, p.state)
      case _ => throw IllegalStateException("Cannot compute relative positions if center is not defined")
  
  def relativeNeighbourhood: Neighbour[TwoDimensionalSpace] =
    Neighbour(Cell((0, 0).toPosition, center.get.state), relativePositions)

  private def toAbsolutePosition(cntr: Cell[TwoDimensionalSpace]): Neighbour[TwoDimensionalSpace] =
    import domain.automaton.NeighborRuleUtility.{+, -}
    Neighbour(
      cntr,
      relativePositions.map(c => Cell(c.position + cntr.position, c.state))
    )

  private def resetBuild: Unit =
    i = 0
    j = 0
    center = Option.empty
    cells = List.empty

  def configureAnother(s: State)(config: Neighbourhood2DBuilder ?=> Unit): Neighbourhood2DBuilder =
    resetBuild
    config(using this)
    buildRule(s)
    this

  extension (t: (Int, Int))
    def toPosition: Position[TwoDimensionalSpace] = Position(t.toList)

object Neighbourhood2DBuilder:
  export DSL.*

  def configureNeighborhood(s: State)(config: Neighbourhood2DBuilder ?=> Unit): Neighbourhood2DBuilder =
    given builder: Neighbourhood2DBuilder = Neighbourhood2DBuilder()
    config
    builder.buildRule(s)
    builder

  object DSL:
    def x(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.addCell(None)
    def n(using builder: Neighbourhood2DBuilder):Neighbourhood2DBuilder = builder.nextRow
    def c(s: State)(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.setCenter(s)
    def state(s: State)(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.addCell(Some(s))

    extension (builder: Neighbourhood2DBuilder)
      def |(b: Neighbourhood2DBuilder): Neighbourhood2DBuilder = b
