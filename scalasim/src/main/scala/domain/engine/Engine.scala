package domain.engine

import domain.Environment.Environment
import domain.base.Dimensions.Dimension
import domain.Environment.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.automaton.NeighborRuleUtility

object Engine:
    trait Engine[D <: Dimension, I, O]:
        type Matrix = Environment[D, I, O]#Matrix
        protected def running: Boolean
        def nextIteration: Unit
        def currentState: Matrix
        def start: Unit
        def stop: Unit

object Engine2D:
    import Engine.*
    val iterations: Int = 1000

    def apply(environment: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]):
         Engine[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] =
        Engine2DImpl(environment)

    private case class Engine2DImpl(
        val environment: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]])
     extends Engine[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        var running = false
        override def nextIteration: Unit = ??? 
        override def currentState: Matrix = ???
        override def stop: Unit = ???
        override def start: Unit = ???