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
        /**
          * Type on which the Cellular Automaton will save all It's rules.
          */
        type Rules
        /**
          * Collection of all the Cellular Automaton's rules.
          */
        protected def ruleCollection: Rules
        /**
          * Apply a specific rule that is based on the input cell state and It's neighbours.
          * @param cell input cell on which the Cellular Automaton will apply It's logic base on the Cell's State or other informations.
          * @param neighbors neighbours of the cell, this will be usefull if we have to check the state of other cells around the input cell.
          * @return the new Cell after applying the rule.
          */
        def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D]
        /**
          * Getter for all the Cellular Automaton's rules.
          * @return the collection of all the Cellular Automaton's rules.
          */
        def rules: Rules
        /**
          * Add a new Rule inside the current Cellular Automaton.
          * @param rule input rule to add inside the Cellular Automaton's rule collection.
          */
        def addRule(rule: NeighbourRule[D]): Unit
    
    trait ComplexCellularAutomaton[D <: Dimension] extends CellularAutomaton[D]:
      override def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Iterable[Cell[D]]
      override def addRule(rule: MultipleOutputNeighbourRule[D]): Unit

    /**
      * Trait in which the type Rules is represented by a Map of: [[State]] -> [[Rule]]
      */
    trait MapSingleRules[D <: Dimension] extends CellularAutomaton[D]:
      override type Rules = Map[State, NeighbourRule[D]]
      override def applyRule(cell: Cell[D], neighbors: Neighbour[D]) = 
        ruleCollection.get(cell.state) match
          case Some(rule) => rule.applyTransformation(neighbors)
          case None => cell
    /**
      * Trait that will be used in more complex Cellular Automatons where mapped on a single state there can be multiple rules to apply on a
      * single cell.
      */
    trait MapMultipleRules[D <: Dimension]  extends CellularAutomaton[D]:
      override type Rules = Map[State, Set[NeighbourRule[D]]]
      override def applyRule(cell: Cell[D], neighbors: Neighbour[D]): Cell[D] =
        ruleCollection.get(cell.state) match
          case None => cell
          case Some(rules) => rules.map(_.applyTransformation(neighbors)).find(_ != cell) match
            case Some(Cell(p, s)) => Cell(p, s)
            case _ => cell
    /**
      * Trait that represent a Multiple Rule Cellular Automaton were mapped on a single Cell's state there will be multiple rules to apply.
      */
    trait MultipleRuleCellularAutomaton[D <: Dimension] extends CellularAutomaton[D] with MapMultipleRules[D]:
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
          
