package dsl.automaton

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.CellularAutomaton.State
import domain.automaton.Neighbour
import domain.automaton.NeighbourRule

import scala.annotation.targetName

/**
 * An object for creating a [[NeighbourRule]] in a two dimensional space.
 * The rule is built using a DSL, whose syntax is contained in [[ExplicitNeighbourRuleBuilder.DSL]].
 *
 * The usage of the DSL is aimed into simplifying rules for making a cell
 * transition from a state to another.
 *
 * @example
 * {{{
 * NeighbourRule2DBuilder.configureNeighborhood(dead):
 *    state(alive) | x        | state(dead) | n |
 *     x            | c(alive) | x            | n |
 *     state(alive) | x        | state(dead)
 * }}}
 * In this example, we specify the DSL block through the `configureNeighbourhood` function,
 * indicating the state that the cell to which the function is applied (the center) will have
 * if the neighbourhood configuration matches with the rule.
 * For more information about the syntax, visit [[ExplicitNeighbourRuleBuilder.DSL]].
 */
trait ExplicitNeighbourRuleBuilder extends NeighbourRuleBuilder[TwoDimensionalSpace]:
    def center: Option[Cell[TwoDimensionalSpace]]
    def cells: List[Cell[TwoDimensionalSpace]]

    /**
     * Make the builder consider the next row of the two dimensional grid.
     *
     * @return this [[ExplicitNeighbourRuleBuilder]]
     */
    def nextRow: ExplicitNeighbourRuleBuilder

    /**
     * Adds a cell to the current set of cells, associating the cell with current row and column indexes.
     *
     * @param s an [[Option]] representing the state of the cell.
     * @return this [[ExplicitNeighbourRuleBuilder]]
     */
    def addCell(s: Option[State]): ExplicitNeighbourRuleBuilder

    /**
     * Sets the center of this neighbourhood, i.e. the cell to which the transformation function is applied.
     *
     * @param s the [[State]] of the center cell.
     * @return this [[ExplicitNeighbourRuleBuilder]]
     */
    def setCenter(s: State): ExplicitNeighbourRuleBuilder

    /**
     * Compute DSL-specified cells positions relative to the center position, i.e. where a given position
     * is placed, if the center is considered to be the origin of the coordinates (0, 0).
     *
     * @example
     * {{{
     *   cellsPositions => ((0, 0), (0, 2))
     *   center => (0, 1)
     *   relativePositions(center) => ((0, -1), (0, 1))
     * }}}
     *
     * @return a list of cells with positions relative to the center cell.
     */
    def relativePositions: List[Cell[TwoDimensionalSpace]]

    /**
     * Same as [[ExplicitNeighbourRuleBuilder.relativePositions]], but builds a [[Neighbour]] object.
     *
     * @return a [[Neighbour]] with relative positions.
     */
    def relativeNeighbourhood: Neighbour[TwoDimensionalSpace]

    /**
     * Converts the given positions and center into a [[NeighbourRule]] representing
     * the desired behaviour, and adds to the set of rules built with this builder.
     *
     * @param s the [[State]] of the center cell if the transformation function can be applied
     *          (i.e. if a neighbourhood matches this rule's neighbourhood).
     */
    def buildRule(s: State): Unit

    /**
     * Builds and adds another [[NeighbourRule]] through the DSL of this current [[ExplicitNeighbourRuleBuilder]].
     *
     * @param s the state that will be assigned to the center if the rule matches.
     * @param config the configuration block that makes use of the DSL.
     * @return this [[ExplicitNeighbourRuleBuilder]] with the new [[NeighbourRule]] added.
     */
    def configureAnother(s: State)(config: ExplicitNeighbourRuleBuilder ?=> Unit): ExplicitNeighbourRuleBuilder

object ExplicitNeighbourRuleBuilder:
  export CustomNeighbourhoodDSL.*

  def apply(): ExplicitNeighbourRuleBuilder = ExplicitNeighbourBuilderImpl()

  def configureRule(s: State)(config: ExplicitNeighbourRuleBuilder ?=> Unit): ExplicitNeighbourRuleBuilder =
    given builder: ExplicitNeighbourBuilderImpl = ExplicitNeighbourBuilderImpl()
    config
    builder.buildRule(s)
    builder

  /**
   * Definition of a Domain Specific Language for configuring a [[NeighbourRule]] making use of
   * [[ExplicitNeighbourRuleBuilder]].
   */
  object CustomNeighbourhoodDSL:
    /**
     * Specify an empty cell
     *
     * @param builder the [[ExplicitNeighbourRuleBuilder]] representing the current configuration.
     * @return the input [[ExplicitNeighbourRuleBuilder]] with an empty cell added.
     */
    def x(using builder: ExplicitNeighbourRuleBuilder): ExplicitNeighbourRuleBuilder = builder.addCell(Option.empty)

    /**
     * Specify the center cell of the current rule. The center of the rule is the center
     * of the neighbourhood and the transformation function will be applied to this cell.
     *
     * N.B. There only can be one center for rule.
     *
     * @param s       the current state of the center cell.
     * @param builder the [[ExplicitNeighbourRuleBuilder]] representing the current configuration.
     * @return the input [[ExplicitNeighbourRuleBuilder]] with the center set.
     */
    def c(s: State)(using builder: ExplicitNeighbourRuleBuilder): ExplicitNeighbourRuleBuilder = builder.setCenter(s)

    /**
     * Specify a neighbour cell with the provided state. The neighbours, together with  the cent, will determine
     * if the generated rule will match a given neighbourhood.
     *
     * @param s       the current state of the neighbour cell.
     * @param builder the [[ExplicitNeighbourRuleBuilder]] representing the current configuration.
     * @return the input [[ExplicitNeighbourRuleBuilder]] with an empty cell added.
     */
    def state(s: State)(using builder: ExplicitNeighbourRuleBuilder): ExplicitNeighbourRuleBuilder = builder.addCell(Some(s))

    /**
     * Specify a newline. This is used for the representation of the grid in order to insert
     * positions that are below the current line. Representing the rule in multiple lines
     * will not affect the rule composition, so this spacial character is needed to add a new line to the rule.
     *
     * @example
     * The newline character should be used as shown in the following example:
     * {{{
     * NeighbourRule2DBuilder.configureNeighborhood(dead):
     *    state(alive) | x        | state(dead) | n |
     *     x            | c(alive) | x            | n |
     *     state(alive) | x        | state(dead)
     * }}}
     * Basically, when reaching the end of line, just add `| n |` for representing a new line.
     * @param builder the [[ExplicitNeighbourRuleBuilder]] representing the current configuration.
     * @return the input [[ExplicitNeighbourRuleBuilder]] with the number of lines incremented by one.
     */
    def n(using builder: ExplicitNeighbourRuleBuilder): ExplicitNeighbourRuleBuilder = builder.nextRow

    extension (builder: ExplicitNeighbourRuleBuilder)
      @targetName("separator") def |(b: ExplicitNeighbourRuleBuilder): ExplicitNeighbourRuleBuilder = b

  private class ExplicitNeighbourBuilderImpl extends ExplicitNeighbourRuleBuilder:

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
        case Some(state) => cells = cells :+ Cell(Position(i, j), state)
        case _ =>
      j += 1
      this

    override def setCenter(s: State): this.type =
      center = Some(Cell(Position(i, j), s))
      j += 1
      this

    override def build: Iterable[NeighbourRule[TwoDimensionalSpace]] = rules

    override def buildRule(s: State): Unit =
      if center.isEmpty then throw IllegalStateException("Cannot build rule without a center!")

      import domain.automaton.NeighborRuleUtility.*

      rules = rules + NeighbourRule(Some(center.get.state))((n: Neighbour[TwoDimensionalSpace]) =>
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
      Neighbour(Cell(Position(0, 0), center.get.state), relativePositions)

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
      val otherBuilder = ExplicitNeighbourBuilderImpl()
      config(using otherBuilder)
      otherBuilder.buildRule(s)
      addRule(otherBuilder.rules.head)
      this

    override def addRule(nr: NeighbourRule[TwoDimensionalSpace]): Unit = rules = rules + nr
