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
      * Engine of the simulation, this trait is responsible for the Environment, It allows to execute the different steps
      * for the simulation. The engine is defined in [[Dimension]] that must be equal to the Environment's dimension.
      * The engine has also a History that is defined with a LazyList, inside this data structure It is possible to
      * find all the different matrix states, in this way there is no need to do polling for the matrix state, but at the end of the
      * simulation It will be possible to get the simulation's history. The Engine has another generic which is [[R]] and It defines
      * the type of the Matrix that will be used for the matrix representation.
      */
    trait Engine[D <: Dimension, R]:
        var running: Boolean
        def history: LazyList[R]
        protected def environment(): Environment[D]
        protected def nextIteration: Unit
        protected def saveInHistory: Unit
        def currentMatrix: R
        def startEngine: Unit
        def stopEngine: Unit
    /**
      * This trait represent a specific type of 2D engine where the matrix type is in the form:
      * [[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]].
      */
    trait IterableEngine2D extends Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]]:
        override def history: LazyList[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
        override def currentMatrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]]
    /**
      * 
      */
    trait IterableThreadEngine2D extends Thread with IterableEngine2D:
        def env: Environment[TwoDimensionalSpace]
        override protected def environment(): Environment[TwoDimensionalSpace] = 
            this.synchronized:
                env
        override def startEngine: Unit =
            if (!running)
                running = true
                start()
    /**
      * 
      */
    trait IterableTimerEngine2D extends IterableEngine2D:
        def timer: Int
        private val ONE_SECOND = 1_000
        protected def startTimer: Unit = 
            var currentTimer = 0
            running = true
            while (currentTimer < timer && running) do 
                saveInHistory
                nextIteration
                currentTimer = currentTimer + 1
                Thread.sleep(ONE_SECOND)
            stopEngine

object Engine2D:
    import Engine.*
    
    val iterations: Int = 1000

    def apply(environment: Environment[TwoDimensionalSpace],
        tick: Int):
         Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
        SimulationEngine2D(environment, tick)

    private case class SimulationEngine2D(val env: Environment[TwoDimensionalSpace], private val tick: Int) extends IterableThreadEngine2D:
        require(tick >= 100)
        @volatile var running = false
        val dimension = env.dimension
        var history = LazyList()
        override def nextIteration: Unit = 
            environment().matrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
                    .flatMap(iterable => iterable.map(cell => cell))
                    .map(cell => env.applyRule(cell, env.neighbours(cell)))
            saveInHistory
        override protected def saveInHistory: Unit = 
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
    // private case class TimerEngine2D(val env: Environment[TwoDimensionalSpace], val timer: Int) 
    //     extends IterableThreadEngine2D with IterableTimerEngine2D