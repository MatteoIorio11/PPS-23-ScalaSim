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
    /**
      * Engine of the simulation, this trait is responsible for the Environment. The engine is defined in 
      * [[Dimension]] that must be equal to the Environment's dimension. The engine has also a 
      */
    trait Engine[D <: Dimension, R]:
        def running: Boolean
        def history: LazyList[R]
        protected def environment(): Environment[D]
        protected def nextIteration: Unit
        def currentMatrix: R
        def startEngine: Unit
        def stopEngine: Unit
    trait IterableEngine2D extends Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]]:
        override def history: LazyList[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
        override def currentMatrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]]

object Engine2D:
    import Engine.*
    val iterations: Int = 1000

    def apply(environment: Environment[TwoDimensionalSpace],
        tick: Int):
         Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
        SimulationEngine2D(environment, tick)

    private case class SimulationEngine2D(
        val env: Environment[TwoDimensionalSpace],
        private val tick: Int)
     extends Thread with IterableEngine2D:
        require(tick >= 100)
        @volatile var running = false
        val dimension = env.dimension
        var history = LazyList()
        override def nextIteration: Unit = 
            environment().matrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
                    .flatMap(iterable => iterable.map(cell => cell))
                    .map(cell => env.applyRule(cell, env.neighbours(cell)))
            saveInHistory
        override def environment(): Environment[TwoDimensionalSpace] = 
            this.synchronized:
                env
        private def saveInHistory: Unit = 
            history = history:+(currentMatrix)
        override def currentMatrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]] = 
            environment().currentMatrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
        override def stopEngine: Unit = 
            running = false
        override def startEngine: Unit = 
            if (!running)
                running = true
                start()
        override def run(): Unit = 
            saveInHistory
            while (running)
                nextIteration
                Thread.sleep(tick)