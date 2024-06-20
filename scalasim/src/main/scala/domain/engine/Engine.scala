package domain.engine

import domain.Environment.Environment
import domain.base.Dimensions.Dimension
import domain.Environment.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell

object Engine:
    trait Engine[D <: Dimension, I, O]:
        def environment: Environment[D, I, O]
        def nextIteration: Unit
        def currentState: Matrix

object Engine2D:
    import Engine.*
    private case class Engine2D(
        val environment: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]])
     extends Engine[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:

        override def nextIteration: Unit = ??? 

        override def currentState: Matrix = ???



