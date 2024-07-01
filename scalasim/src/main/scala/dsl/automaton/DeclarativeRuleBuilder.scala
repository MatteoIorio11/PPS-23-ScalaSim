package dsl.automaton

import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.automaton.Cell
import domain.automaton.NeighbourRule
import domain.automaton.Neighbour
import domain.automaton.CellularAutomaton.State
import domain.automaton.NeighborRuleUtility.getCircularNeighbourhoodPositions
import domain.automaton.NeighborRuleUtility.getNeighboursWithState
import dsl.automaton.AnyState

/**
 * This trait represents a [[NeighbourRuleBuilder]] that supports building a [[NeighbourRule]]
 * in a declarative fashion.
 *
 * Through this builder, it is possible to build a [[NeighbourRule]] that maps a set of rules
 * based on the neighbourhood of a [[Cell]]. Through this rule it is possible to specify:
 * a [[State]] that will represent the final state of the cell if its neighbourhood matches the rule,
 * a number of neighbours (in any position), at a given radius (circular neighbourhood) and with
 * a given state. Moreover, it is possible to specify the initial state of the center cell
 * in order to make the rule match the provided neighbourhood.
 *
 * @example
 * {{{
 * ExpressionRuleBuilder.configureRules:
 *       alive when atLeastSurroundedBy(2) withState(alive) withRadius(1) whenCenterIs(AnyState)
 * }}}
 * In the above example, a [[NeighbourRule]] is created in a declarative fashion,
 * specifying the desired output state if the generic condition (`atLeastSurroundedBy(2)`)
 * on the neighbourhood surrounding the cell. Moreover, the rules states that if at least
 * two neighbours in a radius of two cells from the center have a state `alive`, regardless
 * of center state (`whenCenterIs(AnyState)`), the center cell will transition to state `alive`.
 * N.B. `alive` is just an example state, so make sure you provide a valid state.
 */
trait DeclarativeRuleBuilder extends NeighbourRuleBuilder[TwoDimensionalSpace]:
  /**
   * Sets the initial state of the center cell. If not configured, it is reasonable to assume that the state is
   * [[DeclarativeRuleBuilder.ExpressionRuleDSL.AnyState]].
   *
   * @param s the initial state of the center cell.
   * @return this builder.
   */
  def setInitialState(s: State): this.type

  /**
   * Sets the final state of the center cell. The final state is the state assigned to the center
   * cell if this rule is applied successfully to the given neighbourhood.
   *
   * @param s the final state of the center cell.
   * @return this builder.
   */
  def setFinalState(s: State): this.type

  /**
   * Sets the number of neighbours that must have the state specified in [[setNeighbourState()]].
   * The input parameter is function `(Int => Boolean)` in order to cover the cases where
   * at least or exactly N neighbours must have the given state.
   *
   * @param count the function that represents how many neighbours must have the set state.
   * @return this builder.
   *
   * @example
   * {{{
   *   setNumNeighbours(_ >= 10) // at least 10 neighbours
   *   setNumNeighbours(_ == 10) // exactly 10 neighbours
   * }}}
   */
  def setNumNeighbours(count: Int => Boolean): this.type

  /**
   * Sets the state that the neighbours must have in order for the rule to hold.
   * @param s the state of the neighbours.
   * @return this builder.
   */
  def setNeighbourState(s: State): this.type

  /**
   * Sets the radius of the neighbourhood, i.e. how far the rule should check for the N neighbours
   * with the set state.
   *
   * @param radius the neighbour radius from the center.
   * @return this builder.
   */
  def setNeighboursRadius(radius: Int): this.type

/**
 * Companion object of [[DeclarativeRuleBuilder]] trait.
 */
object DeclarativeRuleBuilder:
  export ExpressionRuleDSL.*

  def apply(): DeclarativeRuleBuilder = DeclarativeBuilderImpl()

  /**
   * Configure a set of rules in a declarative fashion inside the given block.
   *
   * @param config the rule's configuration block.
   * @return this builder.
   */
  def configureRules(config: DeclarativeRuleBuilder ?=> Unit): DeclarativeRuleBuilder =
    given builder: DeclarativeRuleBuilder = DeclarativeRuleBuilder()
    config
    builder

  /**
   * Domain Specific Language for configuring the rules in a declarative fashion.
   */
  object ExpressionRuleDSL:

    /**
     * Used when a needing to specify an **exact** amount of neighbours that must have the same state
     * specified in this [[builder]].
     *
     * @param n the number of exactly how many neighbours should have the same state.
     * @param builder the implicit context (i.e. the builder).
     * @return this builder.
     */
    def surroundedBy(n: Int)(using builder: DeclarativeRuleBuilder): DeclarativeRuleBuilder =
      builder.setNumNeighbours(_ == n)

    /**
     * Used when needing to specify at least how many neighbours must have the same state specified
     * in this [[builder]].
     *
     * @param n the number of how many neighbours should at least have the same state.
     * @param builder the implicit context (i.e. the builder).
     * @return this builder.
     */
    def atLeastSurroundedBy(n: Int)(using builder: DeclarativeRuleBuilder): DeclarativeRuleBuilder =
      builder.setNumNeighbours(_ >= n)

    /**
      * Used when needing to specify that the rule matches if less than [[n]] neighbours
      * have the specified state.
      *
     * @param n the number of how many neighbours should have the same state.
     * @param builder the implicit context (i.e. the builder).
     * @return this builder.
      */
    def fewerThan(n: Int)(using builder: DeclarativeRuleBuilder): DeclarativeRuleBuilder =
      builder.setNumNeighbours(_ < n)

    /**
     * Placeholder for ending a declarative block, avoiding to compose illegal syntax.
     */
    enum EndClause:
      case Radius
      case CenterState

      /**
       * Optional configuration that sets a condition on the center state.
       *
       * Note: this configuration will end the building phase for this rule (note `Unit`` as return value).
       *
       * @param s the initial state of the center.
       * @param builder the implicit context (i.e. the builder).
       */
      infix def whenCenterIs(s: State)(using b: DeclarativeRuleBuilder): Unit =
        b.setInitialState(s)

  /**
   * Extension methods used in combination with [[ExpressionRuleDSL]].
   */
  object DSLExtensions:
    extension (builder: DeclarativeRuleBuilder)

      /**
       * Specify the state of the neighbours. This method is used after [[when()]] method.
       *
       * @param s the state of the neighbours.
       * @param b the implicit context (i.e. the builder).
       * @return this builder.
       */
      infix def withState(s: State)(using b: DeclarativeRuleBuilder): DeclarativeRuleBuilder =
          builder.setNeighbourState(s)

      /**
       * Specify the radius of the neighbourhood, computed from the center of the neighbourhood.
       * The radius is the amount of cells that there are in every direction from the center.
       * E.g. a radius of 1 will yield 8 cells (9 cells - the center), 2 will yield 24 cells, ...
       *
       * This function returns an [[EndClause]], meaning that the builder now have only optional
       * parameters, while mandatory ones should have been already set.
       *
       * @param radius the neighbourhood radius from the center.
       * @param b the implicit context (i.e. the builder).
       * @return an [[EndClause]]
       */
      infix def withRadius(radius: Int)(using b: DeclarativeRuleBuilder): EndClause =
        import EndClause.*
        builder.setNeighboursRadius(radius)
        Radius

      /**
       * Specify the state that the center must have in order for the rule to match.
       *
       * @param s the initial state of the center.
       * @param b the implicit context (i.e. the builder).
       */
      infix def whenCenterIs(s: State)(using b: DeclarativeRuleBuilder): Unit =
        b.setInitialState(s)

    extension (s: State)

      /**
       * Entrypoint for configuring a [[DeclarativeRuleBuilder]] starting from a state.
       * The state that applies this function will represent the state that the center
       * cell of the [[Neighbour]] will have if the rule matches (i.e. it applies successfully).
       *
       * @param builder the set of statements for creating a new [[NeighbourRule]]
       * @return the builder.
       */
      infix def when(builder: => DeclarativeRuleBuilder): DeclarativeRuleBuilder =
        builder.setFinalState(s)

      /**
       * Entrypoint for configuring an [[ExplicitNeighbourRuleBuilder]], used for specifying
       * an exact neighbour locations through [[ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL]].
       * Once the configuration is completed, the generated rule will be added to [[builder]] set of rules.
       *
       * @param neighPlacement the neighbourhood placement that this rule will check.
       * @param builder the implicit context (i.e. the builder).
       * @return the builder.
       */
      infix def whenNeighbourhoodIsExactlyLike(neighPlacement: ExplicitNeighbourRuleBuilder ?=> ExplicitNeighbourRuleBuilder)(using builder: DeclarativeRuleBuilder): DeclarativeRuleBuilder =
        builder.addRule(
          ExplicitNeighbourRuleBuilder.configureRule(s)(neighPlacement).rules.head
        )
        builder

  /**
   * Record for holding configuration for the [[NeighbourRule]] generated by a [[DeclarativeRuleBuilder]].
   *
   * @param initialState the inital state of [[Neighbour]]'s center.
   * @param finalState the final state of [[Neighbour]]'s center if the rule matches.
   * @param numNeighbours number of neighbours that must have the state specified in [[neighboursState]].
   * @param neighboursState the state that a number of neighbours must have.
   * @param neighbourRadius the radius of the research of neighbours with provided state.
   */
  private case class NeighbourRuleConfig(
      var initialState: State = AnyState,
      var finalState: Option[State] = Option.empty,
      var numNeighbours: Option[Int => Boolean] = Option.empty,
      var neighboursState: Option[State] = Option.empty,
      var neighbourRadius: Int = 1,
  ):
    /**
     * Check build parameters
     * @throws java.lang.AssertionError if some mandatory parameters are not set or
     * neighbour radius is negative or major than max integer.
     */
    def checkParameters: Unit =
      assert(
        (finalState, numNeighbours, neighboursState).toList forall (_.nonEmpty),
        "Cannot build without setting all parameters first!"
      )
      assert(neighbourRadius >= 1 & neighbourRadius < Int.MaxValue, s"Neighbour radius must be from 1 to ${Int.MaxValue}")

  private class DeclarativeBuilderImpl extends DeclarativeRuleBuilder:

    private var _rules: Set[NeighbourRule[TwoDimensionalSpace]] = Set.empty

    private var currentConfig: Option[NeighbourRuleConfig] = None

    private var neighbourRulesConfigs: List[NeighbourRuleConfig] = List.empty

    override def rules: Set[NeighbourRule[TwoDimensionalSpace]] = _rules

    override def addRule(nr: NeighbourRule[TwoDimensionalSpace]): Unit = _rules = _rules + nr

    override def setInitialState(s: State): this.type =
      currentConfig = currentConfig.map(_.copy(initialState = s))
      this

    override def setFinalState(s: State): this.type =
      currentConfig =  currentConfig.map(_.copy(finalState = Some(s)))
      this

    override def setNumNeighbours(count: Int => Boolean): this.type =
      currentConfig match
        case Some(config) => buildNextRule
        case None =>
      currentConfig = Some(NeighbourRuleConfig(numNeighbours = Some(count)))
      this

    override def setNeighbourState(s: State): this.type =
      currentConfig = currentConfig.map(_.copy(neighboursState = Some(s)))
      this

    override def setNeighboursRadius(radius: Int): this.type =
      currentConfig = currentConfig.map(_.copy(neighbourRadius = radius))
      this

    override def build: Iterable[NeighbourRule[TwoDimensionalSpace]] =
      buildNextRule
      configureRules
      rules

    /**
     * Builds and adds to the rules collection, the rules built through the list of
     * [[NeighbourRuleConfig]]s contained in this builder.
     */
    private def configureRules: Unit =
      import domain.automaton.NeighborRuleUtility

      _rules = _rules ++ neighbourRulesConfigs.map(config =>
          val locator = getCircularNeighbourhoodPositions(config.neighbourRadius)
          NeighbourRule(Some(config.initialState)): (n: Neighbour[TwoDimensionalSpace]) =>
            n.center match
                case x if x.state == config.initialState || config.initialState == AnyState =>
                  val expectedNeighbourhood = locator.absoluteNeighboursLocations(n.center.position).toList
                  if n.neighbourhood.map(_.position) forall(expectedNeighbourhood.contains(_))
                  then
                    getNeighboursWithState(config.neighboursState.get, n).size match
                      case x if config.numNeighbours.get(x) => Cell(n.center.position, config.finalState.get)
                      case _ => n.center
                  else n.center
                case _ => n.center
      )

    private def buildNextRule: Unit =
      currentConfig foreach (c => neighbourRulesConfigs = neighbourRulesConfigs :+ c)
