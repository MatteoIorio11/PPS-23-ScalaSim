package dsl.automaton

import domain.base.Dimensions.{ Dimension, TwoDimensionalSpace}
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.automaton.NeighbourRule
import domain.automaton.CellularAutomaton.State
import domain.automaton.Cell
import domain.automaton.Neighbour

trait CellularAutomatonBuilder[D <: Dimension]:
  def setRules(rules: Iterable[NeighbourRule[D]]): this.type
  def build(): CellularAutomaton[TwoDimensionalSpace]

object CellularAutomatonBuilder:

  def apply(): CellularAutomatonBuilder[TwoDimensionalSpace] = CellularAutomatonBuilder2DImpl()

  def fromRuleBuilder
    (ruleBuilder: NeighbourRuleBuilder[TwoDimensionalSpace] ?=> NeighbourRuleBuilder[TwoDimensionalSpace]) : CellularAutomatonBuilder[TwoDimensionalSpace] =
    given b: DeclarativeRuleBuilder = DeclarativeRuleBuilder()
    ruleBuilder
    val caBuilder = CellularAutomatonBuilder()
    caBuilder.setRules(ruleBuilder.build)
    caBuilder

  private class CellularAutomatonBuilder2DImpl() extends CellularAutomatonBuilder[TwoDimensionalSpace]:

    private var ca: CellularAutomaton[TwoDimensionalSpace] = MultipleRuleCellularAutomaton[TwoDimensionalSpace]()

    override def setRules(rules: Iterable[NeighbourRule[TwoDimensionalSpace]]): this.type =
      rules foreach (ca.addRule(_))
      this

    override def build(): CellularAutomaton[TwoDimensionalSpace] = ca

/**
 * Used when no specific state is needed for the center in order to match with the rules.
 */
object AnyState extends State

class MultipleRuleCellularAutomaton[D <: Dimension] extends CellularAutomaton[D]:
    type Rules = Map[State, Set[NeighbourRule[D]]]

    protected var ruleCollection: Rules = Map()

    override def addRule(rule: NeighbourRule[D]): Unit =
      val cellState = rule.matchingState.getOrElse(AnyState)
      ruleCollection.get(cellState) match
        case Some(rulez) => ruleCollection = ruleCollection + (cellState -> (rulez + rule))
        case None => ruleCollection = ruleCollection + (cellState -> Set(rule))

    override def rules: Rules = ruleCollection

    override def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D] =
      ruleCollection.get(cell.state) match
        case None => cell
        case Some(rulez) => rulez.map(r => r.applyTransformation(neighbors)).filter(_ != cell).headOption match
          case Some(Cell[D](p, s)) => Cell(p, s)
          case _ => cell

object MultipleRuleCellularAutomaton2D:
  def apply(rules: Iterable[NeighbourRule[TwoDimensionalSpace]]): MultipleRuleCellularAutomaton[TwoDimensionalSpace] =
    val ca = MultipleRuleCellularAutomaton[TwoDimensionalSpace]()
    rules foreach(ca.addRule(_))
    ca
