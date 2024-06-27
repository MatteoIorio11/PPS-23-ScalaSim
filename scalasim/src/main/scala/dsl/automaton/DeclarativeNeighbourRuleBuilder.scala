package dsl.automaton

import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.NeighbourRule
import domain.automaton.CellularAutomaton.State

trait DeclarativeNeighbourRuleBuilder extends NeighbourRuleBuilder[TwoDimensionalSpace]

trait ExpressionRuleBuilder extends DeclarativeNeighbourRuleBuilder:
  def setInitialState(s: State): this.type
  def setFinalState(s: State): this.type
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
      infix def when(builder: => ExpressionRuleBuilder): ExpressionRuleBuilder = builder.setFinalState(s)
  
  private class ExpressionRuleBuilderImpl extends ExpressionRuleBuilder:
    import scala.compiletime.uninitialized

    private var _rules: Set[NeighbourRule[TwoDimensionalSpace]] = Set.empty
    private var initialState: State = uninitialized
    private var finalState: State = uninitialized
    private var numNeighbours: Int => Boolean = uninitialized
    private var neighboursState: State = uninitialized
    private var neighbourRadius: Int = 1

    override def rules: Set[NeighbourRule[TwoDimensionalSpace]] = _rules
    override def addRule(nr: NeighbourRule[TwoDimensionalSpace]): Unit = _rules = _rules + nr
    override def setInitialState(s: State): this.type = { initialState = s; this }
    override def setFinalState(s: State): this.type = { finalState = s; this }
    override def setNumNeighbours(count: Int => Boolean): this.type = { numNeighbours = count ; this }
    override def setNeighbourState(s: State): this.type = { neighboursState = s ; this }
    override def setNeighboursRadius(radius: Int): this.type = { neighbourRadius = radius; this }

    override def build(): Iterable[NeighbourRule[TwoDimensionalSpace]] = ???

