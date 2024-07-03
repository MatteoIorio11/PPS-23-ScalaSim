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

    trait GenericCellularAutomaton[D <: Dimension, I, O, R <: Rule[I, O, State]]:
      def applyRule(input: I): O
      def addRule(rule: R): Unit


    /**
     * Cellular automaton trait. It defines all the information about a general automaton.
     * A cellular automaton must define a collection of rules, where inside each rule is stored the logic
     * to apply to a particular cell. The cellular automaton can be seen as a collection of rules,
     * where each rule must be used only in a specific state. The cellular automaton manages the logic for all Its Cells.
     * @param D the [[Dimension]] of the space;
      */
    trait CellularAutomaton[D <: Dimension] extends GenericCellularAutomaton[D, Neighbour[D], Cell[D], NeighbourRule[D]]:
        /**
          * Type on which the Cellular Automaton will save all It's rules.
          */
        type Rules
        /**
          * Collection of all the Cellular Automaton's rules.
          */
        protected def ruleCollection: Rules
        /**
          * Getter for all the Cellular Automaton's rules.
          * @return the collection of all the Cellular Automaton's rules.
          */
        def rules: Rules
    trait ComplexCellularAutomaton[D <: Dimension] extends GenericCellularAutomaton[D, Neighbour[D], Iterable[Cell[D]], MultipleOutputNeighbourRule[D]]:
      override def applyRule(neighbors: Neighbour[D]): Iterable[Cell[D]]
      override def addRule(rule: MultipleOutputNeighbourRule[D]): Unit

    /**
      * Trait in which the type Rules is represented by a Map of: [[State]] -> [[Rule]]
      */
    trait MapSingleRules[D <: Dimension] extends CellularAutomaton[D]:
      override type Rules = Map[State, NeighbourRule[D]]
      override def applyRule(neighbors: Neighbour[D]) = 
        val cell = neighbors.center
        ruleCollection.get(cell.state) match
          case Some(rule) => rule.applyTransformation(neighbors)
          case None => cell
    /**
      * Trait that will be used in more complex Cellular Automatons where mapped on a single state there can be multiple rules to apply on a
      * single cell. The criteria of the rules that is applied is: the first rule that modifies the state or position or both of the input cell.
      */
    trait MapMultipleRules[D <: Dimension] extends CellularAutomaton[D]:
      override type Rules = Map[State, Set[NeighbourRule[D]]]
      override def applyRule(neighbors: Neighbour[D]): Cell[D] =
        val cell = neighbors.center
        ruleCollection.get(cell.state) match
          case None => cell
          case Some(rules) => rules.map(_.applyTransformation(neighbors)).find(_ != cell) match
            case Some(Cell(p, s)) => Cell(p, s)
            case _ => cell

    /**
      * Trait that represent a Multiple Rule Cellular Automaton were mapped on a single Cell's state there will be multiple rules to apply.
      */
    trait MultipleRuleCellularAutomaton[D <: Dimension] extends MapMultipleRules[D]:
      override def rules: Rules = ruleCollection
    /**
      * Factory for Multiple Rules Cellular Automaton.
      */
    object MutlipleRulesCellularAutomaton:
      def apply[D <: Dimension](): MultipleRuleCellularAutomaton[D] = MutlipleRulesCellularAutomatonImpl()

      private class MutlipleRulesCellularAutomatonImpl[D <: Dimension] extends MultipleRuleCellularAutomaton[D]:
        protected var ruleCollection: Rules = Map()
        override def addRule(rule: NeighbourRule[D]): Unit =
          val cellState = rule.matcher.getOrElse(AnyState)
          ruleCollection.get(cellState) match
            case Some(rulez) => ruleCollection = ruleCollection + (cellState -> (rulez + rule))
            case None => ruleCollection = ruleCollection + (cellState -> Set(rule))


    object MutliOutputCellularAutomaton:
      def apply[D <: Dimension](): ComplexCellularAutomaton[D] = MutliOutputCellularAutomatonImpl()

      private class MutliOutputCellularAutomatonImpl[D <: Dimension] extends ComplexCellularAutomaton[D]:

        private var rules: Map[State, MultipleOutputNeighbourRule[D]] = Map()

        override def addRule(rule: MultipleOutputNeighbourRule[D]): Unit =
          rules = rules + (rule.matcher.getOrElse(AnyState) -> rule)

        override def applyRule(neighbors: Neighbour[D]): Iterable[Cell[D]] =
          rules.get(neighbors.center.state) match
            case None => Iterable(neighbors.center)
            case Some(rule) => rule.applyTransformation(neighbors)
          