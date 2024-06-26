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
 * The rule is built using a DSL, whose syntax is contained in [[NeighbourRule2DBuilder.DSL]].
 *
 * The usage of the DSL is aimed into simplifying rules for making a cell
 * transition from a state to another.
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
 * For more information about the syntax, visit [[NeighbourRule2DBuilder.DSL]].
 */
class NeighbourRule2DBuilder:

  private var i: Int = 0
  private var j: Int = 0
  // TODO: change access modifiers in order to let tests pass
  var center: Option[Cell[TwoDimensionalSpace]] = Option.empty
  var cells: List[Cell[TwoDimensionalSpace]] = List.empty
  var rules: List[NeighbourRule[TwoDimensionalSpace]] = List.empty

  /**
   * Make the builder consider the next row of the two dimensional grid.
   *
   * @return this [[NeighbourRule2DBuilder]]
   */
  def nextRow: this.type =
    i += 1
    j = 0
    this

  /**
   * Adds a cell to the current set of cells, associating the cell with current row and column indexes.
   *
   * @param s an [[Option]] representing the state of the cell.
   * @return this [[NeighbourRule2DBuilder]]
   */
  def addCell(s: Option[State]): this.type =
    s match
      case Some(state) => cells = cells :+ Cell((i, j).toPosition, state)
      case _ =>
    j += 1
    this

  /**
   * Sets the center of this neighbourhood, i.e. the cell to which the transformation function is applied.
   *
   * @param s the [[State]] of the center cell.
   * @return this [[NeighbourRule2DBuilder]]
   */
  def setCenter(s: State): this.type =
    center = Some(Cell((i, j).toPosition, s))
    j += 1
    this

  /**
   * Converts the given positions and center into a [[NeighbourRule]] representing
   * the desired behaviour, and adds to the set of rules built with this builder.
   *
   * @param s the [[State]] of the center cell if the transformation function can be applied
   *          (i.e. if a neighbourhood matches this rule's neighbourhood).
   */
  private def buildRule(s: State): Unit =
    import domain.automaton.NeighborRuleUtility.*

    rules = rules :+ ((n: Neighbour[TwoDimensionalSpace]) =>
        val currentRuleToAdjustedPositions = toAbsolutePosition(n.center)
        if n == currentRuleToAdjustedPositions
          then Cell(n.center.position, s)
          else n.center
        )

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
  def relativePositions: List[Cell[TwoDimensionalSpace]] =
    import domain.automaton.NeighborRuleUtility.-

    center match
      case Some(c) => cells.map: p =>
        Cell(p.position - c.position, p.state)
      case _ => throw IllegalStateException("Cannot compute relative positions if center is not defined")

  /**
   * Same as [[NeighbourRule2DBuilder.relativePositions]], but builds a [[Neighbour]] object.
   *
   * @return a [[Neighbour]] with relative positions.
   */
  def relativeNeighbourhood: Neighbour[TwoDimensionalSpace] =
    Neighbour(Cell((0, 0).toPosition, center.get.state), relativePositions)

  /**
   * Builds a [[Neighbour]] based on relative neighbourhood built with the DSL, using [[cntr]] as
   * a center and with respect to it, computing the absolute coordinates of all the other neighbours.
   *
   * @param cntr the center of the neighbourhood
   * @return a [[Neighbour]] with absolute coordinates based on [[cntr]] as center.
   */
  private def toAbsolutePosition(cntr: Cell[TwoDimensionalSpace]): Neighbour[TwoDimensionalSpace] =
    import domain.automaton.NeighborRuleUtility.{+, -}
    Neighbour(cntr, relativePositions.map(c => Cell(c.position + cntr.position, c.state)))

  /**
   * Resets building parameters for this builder, without altering rules list.
   */
  private def resetBuild: Unit =
    i = 0
    j = 0
    center = Option.empty
    cells = List.empty

  /**
   * Builds and adds another [[NeighbourRule]] through the DSL of this current [[NeighbourRule2DBuilder]].
   *
   * @param s the state that will be assigned to the center if the rule matches.
   * @param config the configuration block that makes use of the DSL.
   * @return this [[NeighbourRule2DBuilder]] with the new [[NeighbourRule]] added.
   */
  def configureAnother(s: State)(config: NeighbourRule2DBuilder ?=> Unit): NeighbourRule2DBuilder =
    resetBuild
    config(using this)
    buildRule(s)
    this

  extension (t: (Int, Int))
    private def toPosition: Position[TwoDimensionalSpace] = Position(t.toList)

object NeighbourRule2DBuilder:
  export DSL.*

  def configureRule(s: State)(config: NeighbourRule2DBuilder ?=> Unit): NeighbourRule2DBuilder =
    given builder: NeighbourRule2DBuilder = NeighbourRule2DBuilder()
    config
    builder.buildRule(s)
    builder

  /**
   * Definition of a Domain Specific Language for configuring a [[NeighbourRule]] making use of
   * [[NeighbourRule2DBuilder]].
   */
  object DSL:
    /**
     * Specify an empty cell
     *
     * @param builder the [[NeighbourRule2DBuilder]] representing the current configuration.
     * @return the input [[NeighbourRule2DBuilder]] with an empty cell added.
     */
    def x(using builder: NeighbourRule2DBuilder): NeighbourRule2DBuilder = builder.addCell(Option.empty)

    /**
     * Specify the center cell of the current rule. The center of the rule is the center
     * of the neighbourhood and the transformation function will be applied to this cell.
     *
     * N.B. There only can be one center for rule.
     *
     * @param s the current state of the center cell.
     * @param builder the [[NeighbourRule2DBuilder]] representing the current configuration.
     * @return the input [[NeighbourRule2DBuilder]] with the center set.
     */
    def c(s: State)(using builder: NeighbourRule2DBuilder): NeighbourRule2DBuilder = builder.setCenter(s)

    /**
     * Specify a neighbour cell with the provided state. The neighbours, together with  the cent, will determine
     * if the generated rule will match a given neighbourhood.
     *
     * @param s the current state of the neighbour cell.
     * @param builder the [[NeighbourRule2DBuilder]] representing the current configuration.
     * @return the input [[NeighbourRule2DBuilder]] with an empty cell added.
     */
    def state(s: State)(using builder: NeighbourRule2DBuilder): NeighbourRule2DBuilder = builder.addCell(Some(s))

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
     *
     * @param builder the [[NeighbourRule2DBuilder]] representing the current configuration.
     * @return the input [[NeighbourRule2DBuilder]] with the number of lines incremented by one.
     */
    def n(using builder: NeighbourRule2DBuilder):NeighbourRule2DBuilder = builder.nextRow

    extension (builder: NeighbourRule2DBuilder)
      @targetName("separator") def |(b: NeighbourRule2DBuilder): NeighbourRule2DBuilder = b
