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
      var initialState: State = AnyState,
      var finalState: Option[State] = Option.empty,
      var numNeighbours: Option[Int => Boolean] = Option.empty,
      var neighboursState: Option[State] = Option.empty,
      var neighbourRadius: Int = 1,
  ):
    def checkParameters: Unit = 
      assert(
        (finalState, numNeighbours, neighboursState).toList forall (_.nonEmpty),
        "Cannot build without setting all parameters first!"
      )
      assert(neighbourRadius >= 1 & neighbourRadius < Int.MaxValue, s"Neighbour radius must be from 1 to ${Int.MaxValue}")

  private class ExpressionRuleBuilderImpl extends ExpressionRuleBuilder:

    private var _rules: Set[NeighbourRule[TwoDimensionalSpace]] = Set.empty

    private var _finalState: Option[State] = None

    private var currentConfig: Option[NeighbourRuleConfig] = None

    private var neighbourRulesConfigs: List[NeighbourRuleConfig] = List.empty

    override def rules: Set[NeighbourRule[TwoDimensionalSpace]] = _rules

    override def addRule(nr: NeighbourRule[TwoDimensionalSpace]): Unit = _rules = _rules + nr

    override def finalState: Option[State] = currentConfig.map(_.finalState).getOrElse(None)

    override def setInitialState(s: State): this.type =
      println("Setting initial state")
      currentConfig = currentConfig.map(_.copy(initialState = s))
      this

    override def setFinalState(s: State): this.type =
      println("Setting final state")
      _finalState = Some(s)
      currentConfig =  currentConfig.map(_.copy(finalState = _finalState))
      this

    override def setNumNeighbours(count: Int => Boolean): this.type =
      println("Setting numNeighbours")
      currentConfig match
        case Some(config) => neighbourRulesConfigs = neighbourRulesConfigs :+ config
        case None =>
      currentConfig = Some(NeighbourRuleConfig(numNeighbours = Some(count)))
      this 

    override def setNeighbourState(s: State): this.type =
      println("Setting neighbours state")
      currentConfig = currentConfig.map(_.copy(neighboursState = Some(s)))
      this 

    override def setNeighboursRadius(radius: Int): this.type =
      println("Setting neighbours radius")
      currentConfig = currentConfig.map(_.copy(neighbourRadius = radius))
      this 

    override def build(): Iterable[NeighbourRule[TwoDimensionalSpace]] =
      println("Building rulez")
      currentConfig foreach (c => neighbourRulesConfigs = neighbourRulesConfigs :+ c)
      configureRules
      rules

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
