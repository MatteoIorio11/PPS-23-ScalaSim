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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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
      * [[Iterable(Iterable(Cell(TwoDimensionalSpace)))]].
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
        @volatile var running = false
        override protected def environment(): Environment[TwoDimensionalSpace] = 
            this.synchronized:
                env
        override def stopEngine = running = false
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
    /**
      * This trait represent an optimzed way to execute the step for each cuellular automaton's cell. This implementation will use an 
      * Iterable Engine 2D. This trait uses an Executor Service, where each row of the matrix will be assigned to a specific Agent, which is 
      * charge to do every step for It's cells.
      */
    trait IterableFastEngine2D extends IterableEngine2D:
        private case class Agent(var rows: List[Cell[TwoDimensionalSpace]]):
            def execute: Unit = 
                rows.map(cell => environment().applyRule(cell, environment().neighbours(cell)))
        private val nAgents = Math.min(Runtime.getRuntime().availableProcessors() + 1, currentMatrix.size)
        private var agents = List[Agent]()
        private var executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
        initialize // Initialize the Engine
        /**
          * Initialize the agents, by assigning to them a collection of rows.
          */
        private def initialize: Unit = 
            val rows = currentMatrix.head.size
            var map = Map[Int, List[Cell[TwoDimensionalSpace]]]()
            val matrix = environment().currentMatrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]]
            val rowsPerAgent = rows / nAgents
            for (i <- 0 until nAgents)
                val startIndex = i * rowsPerAgent
                val endIndex = Math.min((i + 1) * rowsPerAgent, rows)
                val agentRows = matrix.slice(startIndex, endIndex).head
                map = map + (i -> agentRows.toList)
            // handle possible left overs
            if (rows % nAgents != 0)
                val leftovers = rows % nAgents
                var i = 0
                while (i < leftovers)
                    for (j <- 0 until nAgents)
                        map(j).appendedAll(matrix.toList(rows - i - 1))
                        i = i + 1
                    i = i + 1
            agents = map.values.map(list => Agent(list)).toList
        protected def killEngine: Unit = executor.shutdown()
        override def stopEngine = 
          running = false
          killEngine
        /**
          * Fast implementation of the cellular automaton iteration.
          */
        def fastIteration: Unit =
            executor = Executors.newVirtualThreadPerTaskExecutor()
            agents.foreach(agent => executor.execute(() => agent.execute))
            executor.close()
/**
  * Basic Engine 2D for Cellular Automaton Environment execution.
  */
object Engine2D:
    import Engine.*
    def apply(environment: Environment[TwoDimensionalSpace],
        tick: Int):
         Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
        SimulationEngine2D(environment, tick)

    private case class SimulationEngine2D(val env: Environment[TwoDimensionalSpace], private val tick: Int) extends IterableThreadEngine2D:
        require(tick >= 100)
        var history = LazyList()
        override def run() = 
            saveInHistory
            while (running)
                nextIteration
                Thread.sleep(tick)
/**
  * Timer Engine 2D for Cellular Automaton Environment execution. It must be necessary to specify a timer for the execution
  */
object TimerEngine2D:
    import Engine.*
    def apply(env: Environment[TwoDimensionalSpace], timer: Int): Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
         TimerEngine2D(env, timer)
    private case class TimerEngine2D(val env: Environment[TwoDimensionalSpace], val timer: Int) 
    extends IterableThreadEngine2D with IterableTimerEngine2D:
      require(timer >= 0)
      var history = LazyList()
      override def run() = startTimer
/**
  * Fast Engine 2D for Cellular Automaton Environment execution. Inside this Fast Engine It is used the Timer Engine.
  */
object FastEngine2D:
    import Engine.* 
    def apply(env: Environment[TwoDimensionalSpace], timer: Int): Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] =
         FastEngine2D(env, timer) 
    private case class FastEngine2D(val env: Environment[TwoDimensionalSpace], val timer: Int) 
        extends IterableThreadEngine2D with IterableTimerEngine2D with IterableFastEngine2D:
      var history = LazyList()
      override def nextIteration = fastIteration
      override def run() = startTimer