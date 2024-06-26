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
import domain.engine.Engine.IterableThreadEngine2D
import domain.engine.Engine.IterableTimerEngine2D
import domain.engine.Engine.IterableEngine2D

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
        var history: LazyList[R]
        def env: Environment[D]
        protected def environment(): Environment[D]
        protected def nextIteration: Unit
        protected def saveInHistory: Unit = history = history:+currentMatrix
        def currentMatrix: R
        def startEngine: Unit
        def stopEngine: Unit
    /**
      * This trait represent a specific type of 2D engine where the matrix type is in the form:
      * [[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]].
      */
    trait IterableEngine2D extends Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]]:
        var history: LazyList[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
        override def currentMatrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]] = 
            environment().currentMatrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
        override def nextIteration: Unit = 
            environment().matrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
                    .flatMap(iterable => iterable.map(cell => cell))
                    .map(cell => env.applyRule(cell, env.neighbours(cell)))
            saveInHistory
    /**
      * This trait represent a specific type of 2D engine that runs on a Virtual Thread.
      */
    trait IterableThreadEngine2D extends Thread with IterableEngine2D:
        override protected def environment(): Environment[TwoDimensionalSpace] = 
            this.synchronized:
                env
        override def startEngine: Unit =
            if (!running)
                running = true
                Thread.ofVirtual().start(() => this.run())
    /**
      * This trait represent a timed 2D Engine, in which the simulation should runs for the input
      * timer seconds, and after that the simulation will stop It's execution.
      */
    trait IterableTimerEngine2D extends IterableEngine2D:
        def timer: Int
        private val ONE_SECOND = 1_000
        protected def startTimer: Unit = 
            var currentTimer = 0
            while (currentTimer < timer && running) do 
                saveInHistory
                nextIteration
                currentTimer = currentTimer + 1
                Thread.sleep(ONE_SECOND)
            stopEngine

object Engine2D:
    import Engine.*
    def apply(environment: Environment[TwoDimensionalSpace],
        tick: Int):
         Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
        SimulationEngine2D(environment, tick)

    private case class SimulationEngine2D(val env: Environment[TwoDimensionalSpace], private val tick: Int) extends IterableThreadEngine2D:
        require(tick >= 100)
        @volatile var running = false
        var history = LazyList()
        override def stopEngine: Unit = 
            running = false
        override def run(): Unit = 
            saveInHistory
            while (running)
                nextIteration
                Thread.sleep(tick)
object TimerEngine2D:
    import Engine.*
    def apply(env: Environment[TwoDimensionalSpace], timer: Int): Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
         TimerEngine2D(env, timer)
    private case class TimerEngine2D(val env: Environment[TwoDimensionalSpace], val timer: Int) 
    extends IterableThreadEngine2D with IterableTimerEngine2D:
      require(timer >= 0)
      var running = false
      var history: LazyList[Iterable[Iterable[Cell[TwoDimensionalSpace]]]] = LazyList()
      override def stopEngine: Unit = 
        running = false
      override def run(): Unit = 
        startTimer