package dsl.automaton

import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.automaton.Cell
import domain.automaton.NeighbourRule
import domain.automaton.Neighbour
import domain.automaton.CellularAutomaton.State
import domain.automaton.NeighborRuleUtility.getCircularNeighbourhoodPositions
import domain.automaton.NeighborRuleUtility.getNeighboursWithState

trait DeclarativeNeighbourRuleBuilder extends NeighbourRuleBuilder[TwoDimensionalSpace]

trait ExpressionRuleBuilder extends DeclarativeNeighbourRuleBuilder:
  def setInitialState(s: State): this.type
  def setFinalState(s: State): this.type
  def finalState: Option[State]
  def setNumNeighbours(count: Int => Boolean): this.type
  def setNeighbourState(s: State): this.type
  def setNeighboursRadius(s: Int): this.type
  def isBuilding: Boolean
  def buildNextRule: Unit
  def build(): Iterable[NeighbourRule[TwoDimensionalSpace]] // TODO: Move to [[NeighbourRuleBuilder]]

object ExpressionRuleBuilder:
  export ExpressionRuleDSL.*
  export DSLExtensions.*

  def apply(): ExpressionRuleBuilder = ExpressionRuleBuilderImpl()

  def configureRules(config: ExpressionRuleBuilder ?=> Unit): ExpressionRuleBuilder =
    given builder: ExpressionRuleBuilder = ExpressionRuleBuilder()
    config
    builder

  object ExpressionRuleDSL:
    object AnyState extends State

    def surroundedBy(n: Int)(using builder: ExpressionRuleBuilder): ExpressionRuleBuilder =
      builder.setNumNeighbours(_ == n)

    def atLeastSurroundedBy(n: Int)(using builder: ExpressionRuleBuilder): ExpressionRuleBuilder =
      builder.setNumNeighbours(_ >= n)

    def exactlySurroundedBy(neighPlacement: ExplicitNeighbourRuleBuilder ?=> ExplicitNeighbourRuleBuilder)(using builder: ExpressionRuleBuilder): ExpressionRuleBuilder =
      given b: CustomNeighbourRuleBuilder = CustomNeighbourRuleBuilder()
      neighPlacement(using b)
      neighPlacement.buildRule(builder.finalState.get)
      builder.addRule(neighPlacement.rules.head)
      builder

    enum EndClause:
      case Radius
      case CenterState

      infix def whenCenterIs(s: State)(using b: ExpressionRuleBuilder): Unit =
        b.setInitialState(s)

  object DSLExtensions:
    extension (builder: ExpressionRuleBuilder)
      infix def withState(s: State)(using b: ExpressionRuleBuilder): ExpressionRuleBuilder =
        builder.setNeighbourState(s)

      infix def withRadius(radius: Int)(using b: ExpressionRuleBuilder): EndClause =
        import EndClause.*
        builder.setNeighboursRadius(radius)
        Radius

      infix def whenCenterIs(s: State)(using b: ExpressionRuleBuilder): Unit =
        b.setInitialState(s)

    extension (s: State)
      infix def when(builder: => ExpressionRuleBuilder): ExpressionRuleBuilder =
        builder.setFinalState(s)
  
  private case class NeighbourRuleConfig(
      val initialState: State = AnyState,
      val finalState: Option[State] = Option.empty,
      val numNeighbours: Option[Int => Boolean] = Option.empty,
      val neighboursState: Option[State] = Option.empty,
      val neighbourRadius: Int = 1,
  ):
    def checkParameters: Unit = 
      assert(
        (finalState, numNeighbours, neighboursState).toList forall (_.nonEmpty),
        "Cannot build without setting all parameters first!"
      )
      assert(neighbourRadius >= 1 & neighbourRadius < Int.MaxValue, s"Neighbour radius must be from 1 to ${Int.MaxValue}")

  private class ExpressionRuleBuilderImpl extends ExpressionRuleBuilder:
    import scala.compiletime.uninitialized

    private var _rules: Set[NeighbourRule[TwoDimensionalSpace]] = Set.empty
    private var initialState: State = AnyState
    private var _finalState: Option[State] = Option.empty
    private var numNeighbours: Option[Int => Boolean] = Option.empty
    private var neighboursState: Option[State] = Option.empty
    private var neighbourRadius: Int = 1

    private var _isBuilding: Boolean = false
    def isBuilding_= (value: Boolean) = _isBuilding = value
    override def isBuilding: Boolean = _isBuilding

    private var neighbourRulesConfigs: List[NeighbourRuleConfig] = List.empty

    override def rules: Set[NeighbourRule[TwoDimensionalSpace]] = _rules

    override def addRule(nr: NeighbourRule[TwoDimensionalSpace]): Unit = _rules = _rules + nr

    override def setInitialState(s: State): this.type = { initialState = s; this }

    override def finalState: Option[State] = _finalState

    override def setFinalState(s: State): this.type =
      if isBuilding then
        buildNextRule
      else isBuilding = true
      _finalState = Option(s); this

    override def setNumNeighbours(count: Int => Boolean): this.type = { numNeighbours = Option(count) ; this }

    override def setNeighbourState(s: State): this.type = { neighboursState = Option(s) ; this }

    override def setNeighboursRadius(radius: Int): this.type = { neighbourRadius = radius; this }


    override def build(): Iterable[NeighbourRule[TwoDimensionalSpace]] =
      buildNextRule
      configureRules
      isBuilding = false
      rules

    /**
      * Make this builder prepares itself in order to accept a new rule configuration.
      */
    override def buildNextRule: Unit =
      val config = NeighbourRuleConfig(
        initialState,
        finalState,
        numNeighbours,
        neighboursState,
        neighbourRadius,
      )

      config.checkParameters
      neighbourRulesConfigs = neighbourRulesConfigs :+ config
      resetParameters

    private def configureRules: Unit =
      import domain.automaton.NeighborRuleUtility
      import dsl.automaton.ExpressionRuleBuilder.ExpressionRuleDSL.AnyState

      _rules = _rules ++ neighbourRulesConfigs.map(config =>
          val locator = getCircularNeighbourhoodPositions(config.neighbourRadius)
          new NeighbourRule[TwoDimensionalSpace]:
            override def tFunc(in: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] = in.center match
                case x if x.state == config.initialState || config.initialState == AnyState =>
                  val expectedNeighbourhood = locator.absoluteNeighboursLocations(in.center.position).toList
                  if in.neighbourhood.map(_.position) forall(expectedNeighbourhood.contains(_))
                  then
                    getNeighboursWithState(config.neighboursState.get, in).size match
                      case x if config.numNeighbours.get(x) => Cell(in.center.position, config.finalState.get)
                      case _ => in.center
                  else in.center
                case _ => in.center
          )

    private def resetParameters: Unit =
      initialState = AnyState
      _finalState = Option.empty
      numNeighbours = Option.empty
      neighboursState = Option.empty
      neighbourRadius = 1

