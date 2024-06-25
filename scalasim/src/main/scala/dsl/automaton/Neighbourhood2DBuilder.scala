package dsl.automaton

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.CellularAutomaton.State

class Neighbourhood2DBuilder:

  var i: Int = 0
  var j: Int = 0
  var center: Option[Position[TwoDimensionalSpace]] = Option.empty
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

  def setCenter: this.type =
    center = Some(Position(List(i, j)))
    this

object Neighbourhood2DBuilder:
  export DSL.*

  def configureNeighborhood(config: Neighbourhood2DBuilder ?=> Unit): Neighbourhood2DBuilder =
    given builder: Neighbourhood2DBuilder = Neighbourhood2DBuilder()
    config
    builder

  object DSL:
    def x(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.addCell(None)
    def n(using builder: Neighbourhood2DBuilder):Neighbourhood2DBuilder = builder.nextRow
    def c(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.setCenter
    def state(s: State)(using builder: Neighbourhood2DBuilder): Neighbourhood2DBuilder = builder.addCell(Some(s))

    extension (builder: Neighbourhood2DBuilder)
      def |(b: Neighbourhood2DBuilder): Neighbourhood2DBuilder = b

class Alive extends State:
  val name: String = "alive"

class Dead extends State:
  val name: String = "dead"

@main def main(): Unit =
  import Neighbourhood2DBuilder.*

  val nrb = Neighbourhood2DBuilder.configureNeighborhood:
    state(Alive()) | x | state(Alive()) | n |
    x              | c | x               | n |
    state(Dead())  | x | state(Dead())

  nrb.cells.foreach(x => println(s"${x.position} ->> ${x.state}"))