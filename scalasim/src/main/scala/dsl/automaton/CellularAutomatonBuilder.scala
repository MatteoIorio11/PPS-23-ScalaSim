package dsl.automaton

import domain.base.Dimensions.{ Dimension, TwoDimensionalSpace}
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.automaton.NeighbourRule
import domain.automaton.CellularAutomaton.State
import domain.automaton.Cell
import domain.automaton.Neighbour

trait CellularAutomatonBuilder[D <: Dimension]:
  def setRules(rules: Iterable[NeighbourRule[D]]): Unit
  def build(): CellularAutomaton[TwoDimensionalSpace]

object CellularAutomatonBuilder:
  class MultipleRuleCellularAutomaton[D <: Dimension] extends CellularAutomaton[D]:
      type Rules = Map[State, Set[NeighbourRule[D]]]

      protected var ruleCollection: Rules = Map()

      override def addRule(cellState: State, rule: NeighbourRule[D]): Unit =
        ruleCollection.get(cellState) match
          case Some(rulez) => ruleCollection = ruleCollection + (cellState -> (rulez + rule))
          case None => ruleCollection = ruleCollection + (cellState -> Set(rule))

      override def rules: Rules = ruleCollection

      override def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D] =
        ruleCollection.get(cell.state) match
          case None => cell
          case Some(rulez) => rulez.map(r => r.applyTransformation(neighbors)).filter(_ != cell).head match
            case Cell[D](p, s) => Cell(p, s)
            case _ => cell

  object MultipleRuleCellularAutomaton2D:
    def apply(rules: Iterable[(State, NeighbourRule[TwoDimensionalSpace])]): MultipleRuleCellularAutomaton[TwoDimensionalSpace] =
      val ca = MultipleRuleCellularAutomaton[TwoDimensionalSpace]()
      rules.foreach(entry => ca.addRule(entry._1, entry._2))
      ca

  def apply(): CellularAutomatonBuilder[TwoDimensionalSpace] = CellularAutomatonBuilder2DImpl()

  def fromRuleBuilder
    (ruleStates: State*)
    (ruleBuilder: NeighbourRuleBuilder[TwoDimensionalSpace] ?=> NeighbourRuleBuilder[TwoDimensionalSpace])
    (using caBuilder: CellularAutomatonBuilder[TwoDimensionalSpace]): CellularAutomatonBuilder[TwoDimensionalSpace] =
    given b: DeclarativeRuleBuilder = DeclarativeRuleBuilder()
    ruleBuilder
    val rules = ruleBuilder.build
    caBuilder.setRules(rules)
    caBuilder

  private class CellularAutomatonBuilder2DImpl() extends CellularAutomatonBuilder[TwoDimensionalSpace]:
    private var _rules: Set[NeighbourRule[TwoDimensionalSpace]] = Set.empty

    private var _ca: CellularAutomaton[TwoDimensionalSpace] = new CellularAutomaton[TwoDimensionalSpace]:
      type Rules = Map[State, Set[NeighbourRule[TwoDimensionalSpace]]]

      protected var ruleCollection: Rules = Map()

      override def addRule(cellState: State, rule: NeighbourRule[TwoDimensionalSpace]): Unit =
        ruleCollection.get(cellState) match
          case Some(rulez) => ruleCollection = ruleCollection + (cellState -> (rulez + rule))
          case None => ruleCollection = ruleCollection + (cellState -> Set(rule))

      override def rules: Rules = ruleCollection

      override def applyRule(cell: Cell[TwoDimensionalSpace], neighbors: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
        ruleCollection.get(cell.state) match
          case None => cell
          case Some(rulez) => rulez.map(r => r.applyTransformation(neighbors)).filter(_ != cell).head match
            case Cell[TwoDimensionalSpace](p, s) => Cell(p, s)
            case _ => cell

    override def setRules(rules: Iterable[NeighbourRule[TwoDimensionalSpace]]): Unit = rules foreach (r => _rules = _rules + r)
    override def build(): CellularAutomaton[TwoDimensionalSpace] = ???
