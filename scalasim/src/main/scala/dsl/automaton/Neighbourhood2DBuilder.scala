package dsl.automaton

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.CellularAutomaton.State
import domain.automaton.Neighbour

class Neighbourhood2DBuilder:

  var i: Int = 0
  var j: Int = 0
  var center: Option[Cell[TwoDimensionalSpace]] = Option.empty
  var cells: List[Cell[TwoDimensionalSpace]] = List.empty

  def nextRow: this.type =
    i += 1
    j = 0
    this

  def addCell(s: Option[State]): this.type =
    s match
      case Some(state) => cells = cells :+ Cell(Position(List(i, j)), state)
      case _ =>
    j += 1
    this

  def setCenter(s: State): this.type =
    center = Some(Cell(Position(List(i, j)), s))
    this 

  def neighbourhood: Neighbour[TwoDimensionalSpace] = center match
      case Some(c) => Neighbour(c, cells)
      case _ => throw IllegalStateException("Cannot get neighbourhood because center is not defined")

  def relativePositions: List[Cell[TwoDimensionalSpace]] =
    import domain.automaton.NeighborRuleUtility.-

    center match
      case Some(c) => cells.map: p =>
        Cell(p.position - c.position, p.state)
      case _ => throw IllegalStateException("Cannot compute relative positions if center is not defined")

object Neighbourhood2DBuilder:
  export DSL.*

  def configureNeighborhood(config: Neighbourhood2DBuilder ?=> Unit): Neighbourhood2DBuilder =
    given builder: Neighbourhood2DBuilder = Neighbourhood2DBuilder()
    config
    builder

  object DSL:
    def x(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.addCell(None)
    def n(using builder: Neighbourhood2DBuilder):Neighbourhood2DBuilder = builder.nextRow
    def c(s: State)(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.setCenter(s)
    def state(s: State)(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.addCell(Some(s))

    extension (builder: Neighbourhood2DBuilder)
      def |(b: Neighbourhood2DBuilder): Neighbourhood2DBuilder = b
