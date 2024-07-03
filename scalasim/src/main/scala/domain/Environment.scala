package domain

import scala.collection.mutable.ArrayBuffer
import domain.base.Dimensions.*
import domain.automaton.CellularAutomaton.*
import domain.automaton.Cell.*
import scala.util.Random
import automaton.Cell
import base.Position
import automaton.Neighbour
import scala.collection.concurrent.TrieMap
import java.awt.Color

object Environment:
    /**
      * An Environment has inside It a Cellular Automaton [[CellularAutomaton]] and also a Matrix [[Matrix]],
      * that can be defined when this trait is implemented, by doing this the user is able to decide which
      * logic to use for the matrix abstraction. The Environment is responsible for managing all the cell [[Cell]]
      * that are stored inside the matrix, and for each cell, the Environment apply the right Cellular Automaton rule,
      * based on the input cell and all Its neighbours [[Iterable[Cell[D]]]].
      */
    trait Environment[D <: Dimension]:
      /**
        * Matrix that will be used for representing the environment, where all the cells will be stored.
        */
      type Matrix
      /**
        * (Getter) Return the current matrix stored inside the Environment. This method depends on the Matrix type, so It will be
        * necessary to specify which type of Data Strucuture will be used.
        * @return the current matrix that is stored in the Environment.
        */
      def currentMatrix: Matrix
      /**
        * The cellular automaton that will be used in this Environment, the Cellular Automaton works in the same dimension
        * of the Environment, which is D: [[Dimension]].
        */
      def cellularAutomata: CellularAutomaton[D]
      /**
        * Data strucuture that will be used for manage the Matrix.
        */
      def matrix: Matrix
      /**
        * This method is used for retrieve all the Neighbours of a specific input cell. This method is usefull for the
        * rule application and other operations that can be done on a Cellular Automaton.
        * @param cell input cell for which It is necessary to search the Neighbours.
        * @return A collection where are stored all the neighbours of the input cell.
        */
      def neighbours(cell: Cell[D]): Iterable[Cell[D]]
      /**
        * Space dimension of the Environment.
        */
      def dimension: Tuple
      /**
        * This method is used for saving the input sequence of cells into the matrix.
        * @param cells sequence of cells to be stored inside the current matrix.
        */
      protected def saveCell(cells: Cell[D]*): Unit
      /**
        * This method is used for applying a rule on a specific cell given It's neighbours, this method will return the output cell.
        * @param cell input cell where It is necessary to apply the rule.
        * @param neighbors input cell's neighbours.
        * @return the result cell after applying the specific rule.
        */
      def applyRule(cell: Cell[D], neighbors: Iterable[Cell[D]]): Cell[D] = 
          val newCell = cellularAutomata.applyRule(cell, Neighbour(cell, neighbors))
          saveCell(newCell)
          newCell
      /**
        * Initialise the matrix, with a specific configuration of cells.
        */
      protected def initialise(): Unit
      /**
        * Return a collection of all the available cells given a list of position, this is important because
        * the available cells can be different depending on the Space that It is modelled (Square Env, Cubic Env, Toroid Env).
        * @param positions collection of all the positions that needs to be check.
        * @return a collection of all the available cell with existing position inside the matrix.
        */
      protected def availableCells(positions: Iterable[Position[D]]): Iterable[Cell[D]]
    /**
      * This trait represent a Square Environment 2D.
      */
    trait SquareEnvironment extends Environment[TwoDimensionalSpace]:
        def side: Int
        override def dimension = (side, side)
    /**
      * This trait represent a Cubic Environment 3D. 
      */
    trait CubicEnvironment extends Environment[ThreeDimensionalSpace]:
        def edge: Int
        override def dimension = (edge, edge, edge)
   /**
      * This trait represent a Rectangular Environment 2D.
      */
    trait RectangularEnvironment extends Environment[TwoDimensionalSpace]:
        def width: Int
        def heigth: Int
        override def dimension = (heigth, width)
    /**
      * This trait represent a Toroid Environment 2D.
      */
    trait ToroidEnvironmnt extends RectangularEnvironment:
      /**
        * Extension method for create a new custom mod operation.
        */
      extension (dividend: Int)
        /**
          * Mod operation that is able to manage negative numbers.
          * @param divisor: Number to which the dividend is divided. (a mod b) b is the divisor.
          * @return the mod of the operation (dividend mod divisor)
          */
        infix def /%/(divisor: Int): Int = 
          val result = dividend % divisor
          result match
            case value if value < 0 => result + divisor
            case _ => result
    /**
      * Environment D-dimensional where the matrix is defined as a [[TrieMap]]. This can be used in case It will be necessary
      * to use thread-safe data structures.
      */
    trait TrieMapEnvironment[D <: Dimension] extends Environment[D]:
      override type Matrix = TrieMap[Position[D], Cell[D]]
      override def currentMatrix: TrieMap[Position[D], Cell[D]]
      override def matrix: TrieMap[Position[D], Cell[D]]
    /**
    * Environment 2D, where the matrix is defined as an [[ArrayBuffer(ArrayBuffer)]]. This type of matrix can be 
    * very efficient because it allows us to have an O(1) random time access.
    */
    trait ArrayEnvironment2D extends Environment[TwoDimensionalSpace]:
      protected val MAX_SIZE = 2
        override type Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]
        override def matrix: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]
        override protected def saveCell(cells: Cell[TwoDimensionalSpace]*): Unit = 
          cells
            .filter(cell => cell.position.coordinates.size == MAX_SIZE)
            .foreach(cell => 
              val x = cell.position.coordinates.head
              val y = cell.position.coordinates.last
              matrix(x)(y) = cell)
        override def currentMatrix: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            matrix.deepCopy
        /**
          * Extension method for the deep copy of the matrix.
          */
        extension (array: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]])
            /**
              * This extension method creates a deep copy of the current matrix.
              * @return a copy of the caller.
              */
            def deepCopy: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
                matrix.map(row => row.map(cell => Cell(Position(cell.position.coordinates.toArray*), cell.state)))
    /**
      * This trait represent a Toroid Environment 2D, where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait ArrayToroidEnvironment extends ToroidEnvironmnt with ArrayEnvironment2D:
      override protected def saveCell(cells: Cell[TwoDimensionalSpace]*) = 
        cells.foreach(cell => 
          val x = cell.position.coordinates.head
          val y = cell.position.coordinates.last
          matrix(x /%/ heigth)(y /%/ width) = cell)
      override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]) = 
        positions
          .map(pos => {
            pos.coordinates match
                case head :: next :: tail => List(head /%/ heigth, next /%/ width)
                case Nil => List()
            })
            .filter(pos => pos.size == MAX_SIZE)
            .map(cor => matrix(cor.head)(cor.last))
     /**
        * Extension methods for initialize the Space.
        */
      extension (array: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]])
          /**
            * This extension method initialize the toroid space using the input initial cell, the initial cell
            * will be used only for the input state, all the coordinates will be fixed automatically.
            * @param initialCell: initial cell to use for initialize the space.
            * @return a new Matrix initialized with the input initial cell.
            */
          def initializeSpace(initialCell: Cell[TwoDimensionalSpace]): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            val array = ArrayBuffer.fill(heigth, width)(initialCell)
            for (y <- 0 until width)
                for (x <- 0 until heigth)
                    array(x)(y) = (Cell(Position(x, y), initialCell.state))
            array
          /**
            * This extension method returns a new Matrix in which there a #nCells cells with the input state.
            * @param nCells: number of cells to spawn 
            * @param state: input state to use inside the spawned cells.
            * @return a new Matrix where there are #nCells with the input state.
            */
          def spawnCells(nCells: Int)(state: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            var spawnedCells = 0
            while (spawnedCells < nCells)
                val x = Random.nextInt(heigth) % width
                val y = Random.nextInt(width) % heigth
                val position = Position[TwoDimensionalSpace](x, y)
                if (array(x)(y).state != state)
                    array(x)(y) = (Cell(position, state))
                    spawnedCells = spawnedCells + 1
            array
          /**
            * This extension method returns a new Matrix in which there can be X cells with the inputState
            * otherwise the cell has It's initial state. The selected state is choosen by using a random value
            * where if the random value is True then It is selected the inputState otherwise the original state.
            * @param initialState state used for the matrix initialization.
            * @param spawnState Input state to use If the probability returned from the random value is True.
            * @return a new Matrix in which there can be spawned a number of cell with the input state.
            */
          def spawnCell(initialState: State)(spawnState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), initialState)
            val array = ArrayBuffer.fill(heigth, width)(initialCell)
            for (y <- 0 until width)
              for (x <- 0 until heigth)
                  val probability = Random().nextBoolean()
                  val state = probability match
                      case x if x => spawnState
                      case _ => array(x)(y).state
                  array(x)(y) = Cell(Position(x, y), state)
            array(0)(0) = Cell(Position(0, 0), spawnState)
            array

    /**
      * Square Environment 2D, where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait SquareArrayEnvironment2D extends SquareEnvironment with ArrayEnvironment2D:
        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] =
          positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < side))
            .map(pos => pos.coordinates.toList)
            .filter(cor => cor.size == MAX_SIZE)
            .map(cor => matrix(cor.head)(cor.last))
        /**
          * Utilities methods.
          */
        extension (array: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]])
            /**
              * This extension method can be used for initialize the entire matrix with an initial type of cell,
              * every cell will be placed inside the right coordinates and every cell will have the correct position.
              * @param initialCell initial state of matrix, where all cell are off.
              * @return a new Matrix filled with cells.
              */
            def initializeSpace(state: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
              val cell: Cell[TwoDimensionalSpace] = Cell(Position(-1,-1), state)
              val array = ArrayBuffer.fill(side, side)(cell)
                for (y <- 0 until side)
                  for (x <- 0 until side)
                    array(x)(y) = (Cell(Position(x, y), state))
              array
            /**
              * This extension method can be used for spawn a fixed number of cell inside the Matrix with the input
              * state.
              * @param nCells: number of cells to spawn inside the matrix.
              * @param state: state that will be used inside the spawned cells.
              * @return a new Matrix where there are #nCells with the input state.
              */
            def spawnCells(nCells: Int)(state: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
                var spawnedCells = 0
                while (spawnedCells < nCells)
                    val x = Random.nextInt(side)
                    val y = Random.nextInt(side)
                    val position = Position[TwoDimensionalSpace](x, y)
                    if (array(x)(y).state != state)
                        array(x)(y) = (Cell(position, state))
                        spawnedCells = spawnedCells + 1
                array
            /**
              * This extension method can be used for spawn a random number of cell inside the Matrix with the input state.
              * @param initialState initial state that will be used for the matrix initialization.
              * @param spawnState specific state to set on cells.
              * @return a new Matrix where there are a number of random cells with the input state.
              */
            def spawnCell(initialState: State)(spawnState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
              val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), initialState)
              val array = ArrayBuffer.fill(side, side)(initialCell)
              for (y <- 0 until side)
                for (x <- 0 until side)
                    val probability = Random().nextBoolean()
                    val state = probability match
                        case spawn if spawn => spawnState
                        case _ => array(x)(y).state
                    array(x)(y) = Cell(Position(x, y), state)
              array(0)(0) = Cell(Position(0, 0), spawnState)
              array
            /**
              * 
              * @param nCells
              * @param states
              * @return
              */
            def spawnCell(nCells: Int)(states: State*): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
                var spawnedCells = 0
                while (spawnedCells < nCells)
                    val x = Random.nextInt(side)
                    val y = Random.nextInt(side)
                    val position = Position[TwoDimensionalSpace](x, y)
                    if (!states.contains(array(x)(y).state))
                      array(x)(y) = (Cell(position, states(Random.nextInt(states.size))))
                      spawnedCells = spawnedCells + 1
                array
    /**
      * Rectangula Environment2D where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait RectangularArrayEnvironment2D extends RectangularEnvironment with ArrayEnvironment2D:
      override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]) = 
          positions
            .filter(pos => pos.coordinates.size == MAX_SIZE)
            .filter(pos => pos.coordinates.head >= 0 && pos.coordinates.last < heigth &&
                            pos.coordinates.last >= 0 && pos.coordinates.last < width)
            .map(pos => matrix(pos.coordinates.head)(pos.coordinates.last))
