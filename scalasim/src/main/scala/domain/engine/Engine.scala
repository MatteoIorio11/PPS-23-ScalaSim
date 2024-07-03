package domain.engine

import domain.Environment.{Environment, ArrayEnvironment2D}
import domain.Environment.*
import domain.base.Position
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.automaton.{Neighbour, Cell, NeighborRuleUtility}
import scala.collection.mutable.ArrayBuffer
import domain.engine.Engine.{IterableThreadEngine2D, IterableTimerEngine2D, IterableEngine2D}
import java.util.concurrent.{ExecutorService, Executor, Executors}
import java.awt.Color
import domain.automaton.CellularAutomaton.State

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
        /**
          * This method is based on the used environment, the matrix that will be returned is the environment's matrix deep copy.
          * @return the deep copy of the current matrix.
          */
        def currentMatrix: R
        /**
          * Start the Engine. If the engine is already running nothing is done.
          */
        def startEngine: Unit
        /**
          * Stop the Engine. If the Engine is already stopped nothing is done.
          */
        def stopEngine: Unit
    /**
      * This trait represent a specific type of 2D engine where the matrix type is in the form:
      * [[Iterable(Iterable(Cell(TwoDimensionalSpace)))]].
      */
    trait IterableEngine2D extends Engine[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]]:
        var history: LazyList[Iterable[Iterable[Cell[TwoDimensionalSpace]]]] = LazyList()
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
        /**
          * Start the Engine by creating a new Virtual Thread that is responsible for the engine's execution.
          */
        override def startEngine =
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
        /**
          * Start the Timer, by doing an iteration on each second. The simulation will stop after reaching the maximum input time or by
          * stopping the Engine using the stopEngine method.
          */
        protected def startTimer: Unit = 
            var currentTimer = 0
            while (currentTimer < timer && running) do 
                saveInHistory
                nextIteration
                currentTimer = currentTimer + 1
                Thread.sleep(ONE_SECOND)
            stopEngine
   /**
      * Trait that represent a general View that will be attached to the engine. The view is defined in [[Dimension]].
      */
    trait EngineView[D <: Dimension]:
      def updateView(cells: Iterable[Cell[D]]): Unit
      def dimension: Tuple
    /**
      * Engine with a GUI, this will be used for real time simulation with a GUI.
      */
    trait GUIEngine2D extends IterableEngine2D:
      def view: EngineView[TwoDimensionalSpace]
      def colors: Map[State, Color]
      /**
        * Update the current view attached to this engine.
        */
      def updateView: Unit


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
      override def run() = startTimer
/**
  * 
  */
object GUIEngine2D:
  private val SLEEP_TICK = 500
  private var guiThreads = List[Thread]()
  private val maxSize = Runtime.getRuntime().availableProcessors() + 1
  import Engine.* 
  def apply(env: Environment[TwoDimensionalSpace], view: EngineView[TwoDimensionalSpace]): GUIEngine2D = 
    GUIEngine2DImpl(env, view)
  private case class GUIEngine2DImpl(val env: Environment[TwoDimensionalSpace], val view: EngineView[TwoDimensionalSpace]) 
    extends IterableThreadEngine2D with GUIEngine2D:
    override val colors: Map[State, Color] = Map()
    override def updateView = 
      view.updateView(environment().currentMatrix.asInstanceOf[Iterable[Iterable[Cell[TwoDimensionalSpace]]]].flatMap(it => it.map(cell => cell)))
    override def run() = 
      saveInHistory
      while (running)
        nextIteration
        guiThreads = guiThreads.::(Thread.ofVirtual().start(() => updateView))
        guiThreads.size match
          case x if x == maxSize => 
            guiThreads.foreach(thread => thread.join())
            guiThreads = List()
          case _ => 
        Thread.sleep(SLEEP_TICK)