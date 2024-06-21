package domain.engine

import domain.Environment.Environment
import domain.base.Dimensions.Dimension
import domain.Environment.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.automaton.NeighborRuleUtility
import domain.simulations.gameoflife.GameOfLifeEnvironment

object Engine:
    trait Engine[D <: Dimension, I, O]:
        type Matrix = Environment[D, I, O]#Matrix
        def running: Boolean
        def nextIteration: Unit
        def currentState: Matrix
        def startEngine: Unit
        def stopEngine: Unit

object Engine2D:
    import Engine.*
    val iterations: Int = 1000

    def apply(environment: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]):
         Engine[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] =
        SimulationEngine2D(environment)

    private case class SimulationEngine2D(
        val environment: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]])
     extends Thread with Engine[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        @volatile var running = false
        override def nextIteration: Unit = ???
            
        override def currentState: Matrix = ???
        override def stopEngine: Unit = 
            running = false
        override def startEngine: Unit = 
            if (!running)
                running = true
                start()
        override def run(): Unit = ???
        