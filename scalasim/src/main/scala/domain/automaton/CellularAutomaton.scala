package domain.automaton

import domain.base.Dimensions.*
import domain.automaton.Neighbour
import domain.automaton.Rule
import domain.automaton.Cell.*
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.NeighbourRule

object CellularAutomaton:
    /**
      * Trait that incapsulate the different state of the Cellular Automaton
      */
    trait State

    /**
      * A [[State]] representing a state with an associated value of type [[T]].
      */
    trait ValuedState[T] extends State:
      /**
        *
        * @return the value associated with this state.
        */
      def value: T

      infix def map[B](f: T => B): ValuedState[B] =
        val v: B = f(value)
        new ValuedState[B] { override def value: B = v }

    /**
     * Used when no specific state is needed for the center in order to match with the rules.
     */
    object AnyState extends State

    /**
     * Cellular automaton trait. It defines all the information about a general automaton.
     * A cellular automaton must define a collection of rules, where inside each rule is stored the logic
     * to apply to a particular cell. The cellular automaton can be seen as a collection of rules,
     * where each rule must be used only in a specific state. The cellular automaton manages the logic for all Its Cells.
     * @param D the [[Dimension]] of the space;
      */
    trait CellularAutomaton[D <: Dimension]:
        type Rules
        protected def ruleCollection: Rules
        def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D]
        def rules: Rules
        def addRule(rule: NeighbourRule[D]): Unit

    /**
      * Trait in which the type Rules is represented by a Map of: [[State]] -> [[Rule]]
      */
    trait MapRules2D extends CellularAutomaton[TwoDimensionalSpace]:
      override type Rules = Map[State, NeighbourRule[TwoDimensionalSpace]]

    trait MultipleRuleCellularAutomaton[D <: Dimension] extends CellularAutomaton[D]:
      override type Rules = Map[State, Set[NeighbourRule[D]]]

      override def rules: Rules = ruleCollection

      override def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D] =
        ruleCollection.get(cell.state) match
          case None => cell
          case Some(rules) => rules.map(_.applyTransformation(neighbors)).find(_ != cell) match
            case Some(Cell(p, s)) => Cell(p, s)
            case _ => cell

    object MutlipleRulesCellularAutomaton:
      def apply[D <: Dimension](): MultipleRuleCellularAutomaton[D] = MutlipleRulesCellularAutomatonImpl()

      private class MutlipleRulesCellularAutomatonImpl[D <: Dimension] extends MultipleRuleCellularAutomaton[D]:
        protected var ruleCollection: Rules = Map()
        override def addRule(rule: NeighbourRule[D]): Unit =
          val cellState = rule.matcher.getOrElse(AnyState)
          ruleCollection.get(cellState) match
            case Some(rulez) => ruleCollection = ruleCollection + (cellState -> (rulez + rule))
            case None => ruleCollection = ruleCollection + (cellState -> Set(rule))
          
