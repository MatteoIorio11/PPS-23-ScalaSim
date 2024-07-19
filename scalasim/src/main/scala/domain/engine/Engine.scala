package domain.engine

import domain.Environment.ArrayEnvironment2D
import domain.Environment.*
import domain.base.Position
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.automaton.{Neighbour, Cell, NeighborRuleUtility}
import domain.engine.Engine.{ThreadEngine2D, TimerEngine2D}
import java.util.concurrent.{ExecutorService, Executor, Executors}
import domain.engine.Engine.GeneralEngine

object Engine:
  /**
    * Engine of the simulation, this trait is responsible for the Environment, It allows to execute the different steps
    * for the simulation. The engine is defined in [[Dimension]] that must be equal to the Environment's dimension.
    * The engine has also a History that is defined with a LazyList, inside this data structure It is possible to
    * find all the different matrix states, in this way there is no need to do polling for the matrix state, but at the end of the
    * simulation It will be possible to get the simulation's history. The Engine has another generic which is [[R]] and It defines
    * the type of the Matrix that will be used for the matrix representation.
    *
    * @param D the dimension of the space.
    * @param R Matrix type.
    */
  trait GeneralEngine[D <: Dimension]:
    protected def env: GenericEnvironment[D, ?]
    def environment(): GenericEnvironment[D, ?]
    var running: Boolean

    var history: LazyList[Cell[D]] = LazyList()

    protected def nextIteration: Unit =
        environment().nextIteration
        saveInHistory

    protected def saveInHistory: Unit = history = history.appendedAll(currentMatrix)
    /**
      * This method is based on the used environment, the matrix that will be returned is the environment's matrix deep copy.
      * @return the deep copy of the current matrix.
      */
    def currentMatrix: LazyList[Cell[D]] = environment().currentMatrix
    /**
      * Start the Engine. If the engine is already running nothing is done.
      */
    def startEngine: Unit
    /**
      * Stop the Engine. If the Engine is already stopped nothing is done.
      */
    def stopEngine: Unit

  /**
    * Engine where the environment is defined as a [[SimpleEnvironment]] in [[D]]
    * @param D the dimension of the space.
    * @param R Matrix type.
    */
  trait SimpleEngine[D <: Dimension, R] extends GeneralEngine[D]:
    override def env: SimpleEnvironment[D]
    override def environment(): SimpleEnvironment[D]

  /**
    * This trait represent a specific type of 2D engine that runs on a Virtual Thread.
    */
  trait ThreadEngine2D extends Thread with GeneralEngine[TwoDimensionalSpace]:
    @volatile var running = false
    override def environment(): GenericEnvironment[TwoDimensionalSpace, ?] =
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
  trait TimerEngine2D extends GeneralEngine[TwoDimensionalSpace]:
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
    * Complex Engine, where the environmnet is defined like a [[ComplexEnvironment]] in [[D]].
    * @param D the dimension of the space.
    * @param R Matrix type.
    */
  trait ComplexEngine[D <: Dimension, R] extends GeneralEngine[D]:
    override def env: ComplexEnvironment[D]
    def environment(): ComplexEnvironment[D]

 /**
  * Trait that represent a general View that will be attached to the engine. The view is defined in [[Dimension]].
  *
  * @param D the dimension of the space.
  */
  trait EngineView[D <: Dimension]:
      def updateView(cells: Iterable[Cell[D]]): Unit
      def dimension: Tuple
    /**
      * Engine with a GUI, this will be used for real time simulation with a GUI.
      */

  /**
    * Trait that represent an Engine for Real Time simulation with a GUI, this trait extends from the [[IterableEngine2D]]. By using
    * this trait the user can observe a real time simulation.
    */
  trait GUIEngine2D extends GeneralEngine[TwoDimensionalSpace]:
    /**
      * Attached view to the engine for the real time simulation.
      */
      def view: EngineView[TwoDimensionalSpace]
      /**
        * Update the current view attached to this engine.
        */
      def updateView: Unit

/**
  * Basic Engine 2D for Cellular Automaton Environment execution.
  */
object Engine2D:
    import Engine.*
    def apply(environment: GenericEnvironment[TwoDimensionalSpace, ?],
        tick: Int):
         ThreadEngine2D =
        SimulationEngine2D(environment, tick)

    private case class SimulationEngine2D(val env: GenericEnvironment[TwoDimensionalSpace, ?],
     private val tick: Int) extends ThreadEngine2D:
        require(tick >= 5)
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
    def apply(env: GenericEnvironment[TwoDimensionalSpace, ?], timer: Int): ThreadEngine2D =
         TimerEngine2DImpl(env, timer)
    private case class TimerEngine2DImpl(val env: GenericEnvironment[TwoDimensionalSpace, ?], val timer: Int)
    extends ThreadEngine2D with TimerEngine2D:
      require(timer >= 0)
      override def run(): Unit = startTimer
/**
  * Gui Engine 2D for real time simulation. Factory Object for the Engine.
  */
object GUIEngine2D:
  import Engine.*

  private val SLEEP_TICK = 500
  private var guiThreads = List[Thread]()
  private val maxSize = Runtime.getRuntime().availableProcessors() + 1

  def apply(env: GenericEnvironment[TwoDimensionalSpace, ?], view: EngineView[TwoDimensionalSpace]): GUIEngine2D =
    GUIEngine2DImpl(env, view)

  private case class GUIEngine2DImpl(val env: GenericEnvironment[TwoDimensionalSpace, ?], val view: EngineView[TwoDimensionalSpace])
    extends ThreadEngine2D with GUIEngine2D:
    override def updateView = 
      view.updateView(environment().currentMatrix)
    override def run() = 
      saveInHistory
      guiThreads = (Thread.ofVirtual().start(() => updateView)) :: guiThreads
      while (running)
        nextIteration
        guiThreads = (Thread.ofVirtual().start(() => updateView)) :: guiThreads
        guiThreads.size match
          case `maxSize` => 
            guiThreads.foreach(thread => thread.join())
            guiThreads = List()
          case _ => 
        Thread.sleep(SLEEP_TICK)