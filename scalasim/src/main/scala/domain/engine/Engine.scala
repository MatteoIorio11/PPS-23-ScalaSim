package domain.engine

import domain.Environment.Environment
import domain.base.Dimensions.Dimension
import domain.Environment.ArrayEnvironment2D
import domain.Environment.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.automaton.NeighborRuleUtility
import domain.simulations.gameoflife.GameOfLifeEnvironment
import domain.base.Position
import scala.collection.mutable.ArrayBuffer

object Engine:
    trait Engine[D <: Dimension, I, O, R]:
        def running: Boolean
        protected def environment(): Environment[D, I, O]
        def nextIteration: Unit
        def currentMatrix: R
        def startEngine: Unit
        def stopEngine: Unit
    trait IterableEngine2D[D <: Dimension, I, O] extends Engine[D, I, O, Iterable[Iterable[Cell[TwoDimensionalSpace]]]]:
        override def currentMatrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]]

object Engine2D:
    import Engine.*
    val iterations: Int = 1000

    def apply(environment: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]):
         Engine[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace], Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
        SimulationEngine2D(environment)

    private case class SimulationEngine2D(
        val env: Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]])
     extends Thread with IterableEngine2D[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        @volatile var running = false
        val dimension = env.dimension
        override def nextIteration: Unit = 
            environment().matrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
                    .flatMap(iterable => iterable.map(cell => cell))
                    .map(cell => env.applyRule(cell, env.neighbours(cell)))
        override def environment(): Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] = 
            this.synchronized:
                env
        
        override def currentMatrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]] = 
            environment().matrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
        override def stopEngine: Unit = 
            running = false
        override def startEngine: Unit = 
            if (!running)
                running = true
                start()
        override def run(): Unit = 
            while (running)
                nextIteration
                Thread.sleep(100)