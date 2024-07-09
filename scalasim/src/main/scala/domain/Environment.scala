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
import scala.annotation.init

object Environment:
    /**
      * This trait represent a Generic Environment that It is used for modelling the space in which the
      * Cellular Automata works, more in particular by using this trait It will be possible to apply a specific rule
      * on a specific cell by using a specific neighbourhood. The specific rule that will be used is decided by the
      * Cellular Automata by checking the input cell's state and It's neighbours. Also, by using this trait It will be
      * possible to save the new cell into the space where all the cells are stored.
      * @param D Cellular Automata's space dimension.
      * @param R apply rule return type.
      */
    trait GenericEnvironment[D <: Dimension, R] extends Space[D]:
      protected def saveCell(cells: Cell[D]*): Unit
      def applyRule(cell: Cell[D], neighbors: Iterable[Cell[D]]): R

    /**
      * 
      * @param D
      */
    trait SimpleEnvironment[D <: Dimension] extends GenericEnvironment[D, Cell[D]]:
      def cellularAutomata: CellularAutomaton[D]
      def applyRule(cell: Cell[D], neighbors: Iterable[Cell[D]]): Cell[D] = 
        val newCell = cellularAutomata.applyRule(Neighbour(cell, neighbors))
        saveCell(newCell)
        newCell

    /**
      * TODO
      */
    trait ComplexEnvironment[D <: Dimension] extends GenericEnvironment[D, Iterable[Cell[D]]]:
      def cellularAutomata: ComplexCellularAutomaton[D]
      override def applyRule(cell: Cell[D], neighbors: Iterable[Cell[D]]): Iterable[Cell[D]] = 
          val newCell = cellularAutomata.applyRule(Neighbour(cell, neighbors))
          saveCell(newCell.toSeq*)
          newCell
      
    /**
      * TODO
      */
    trait Space[D <: Dimension]:
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
      def cellularAutomata: GenericCellularAutomaton[D, ?, ?, ?]
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
    trait SquareEnvironment extends GenericEnvironment[TwoDimensionalSpace, ?]:
        def side: Int
        override def dimension: Tuple2[Int, Int] = (side, side)

    /**
      * This trait represent a Cubic Environment 3D. 
      */
    trait CubicEnvironment extends GenericEnvironment[ThreeDimensionalSpace, ?]:
        def edge: Int
        override def dimension = (edge, edge, edge)

   /**
      * This trait represent a Rectangular Environment 2D.
      */
    trait RectangularEnvironment extends GenericEnvironment[TwoDimensionalSpace, ?]:
        def width: Int
        def heigth: Int
        override def dimension: Tuple2[Int, Int] = (heigth, width)

    /**
      * This trait represent a Toroid Environment 2D.
      * TODO
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
    trait TrieMapEnvironment[D <: Dimension] extends GenericEnvironment[D, ?]:
      override type Matrix = TrieMap[Position[D], Cell[D]]
      override def currentMatrix: TrieMap[Position[D], Cell[D]]
      override def matrix: TrieMap[Position[D], Cell[D]]

    /**
    * Environment 2D, where the matrix is defined as an [[ArrayBuffer(ArrayBuffer)]]. This type of matrix can be 
    * very efficient because it allows us to have an O(1) random time access.
    */
    trait ArrayEnvironment2D extends GenericEnvironment[TwoDimensionalSpace, ?]:
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
        * TODO
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
            * This extension method initialize the toroid space using the input initial cell, the initial cell
            * will be used only for the input state, all the coordinates will be fixed automatically.
            * @param initialCell: initial cell to use for initialize the space.
            * @return a new Matrix initialized with the input initial cell.
            */
          protected def generalInitialization(dimension: Tuple2[Int, Int])(initialState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            val cell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), initialState)
            val array = ArrayBuffer.fill(dimension._1, dimension._2)(cell)
            for (y <- 0 until dimension._2)
                for (x <- 0 until dimension._1)
                    array(x)(y) = (Cell(Position(x, y), initialState))
            array
          /**
            * This extension method returns a new Matrix in which there can be X cells with the inputState
            * otherwise the cell has It's initial state. The selected state is choosen by using a random value
            * where if the random value is True then It is selected the inputState otherwise the original state.
            * @param initialState state used for the matrix initialization.
            * @param spawnState Input state to use If the probability returned from the random value is True.
            * @return a new Matrix in which there can be spawned a number of cell with the input state.
            */
          protected def generalSpawn(dimension: Tuple2[Int, Int])(initialState: State)(spawnState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            val initialCell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), initialState)
            val array = ArrayBuffer.fill(dimension._1, dimension._2)(initialCell)
            for (y <- 0 until dimension._2)
              for (x <- 0 until dimension._1)
                  val probability = Random().nextBoolean()
                  val state = probability match
                      case x if x => spawnState
                      case _ => array(x)(y).state
                  array(x)(y) = Cell(Position(x, y), state)
            array(0)(0) = Cell(Position(0, 0), spawnState)
            array
          /**
            * This extension method returns a new Matrix in which there a #nCells cells with the input state.
            * @param nCells: number of cells to spawn 
            * @param state: input state to use inside the spawned cells.
            * @return a new Matrix where there are #nCells with the input state.
            */
          protected def generalMultipleSpawn(dimension: Tuple2[Int, Int])(nCells: Int*)(states: State*): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            if (nCells.size != states.size)
              array
            else
              nCells.zipWithIndex.foreach((nCell, index) => 
                var spawnedCells = 0
                while (spawnedCells < nCell)
                  val x = Random.nextInt(dimension._1)
                  val y = Random.nextInt(dimension._2)
                  val position = Position[TwoDimensionalSpace](x, y)
                  if (states.forall(state => state != array(x)(y).state))
                    array(x)(y) = (Cell(position, states(index)))
                    spawnedCells = spawnedCells + 1
                )
            array

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
          def initializeSpace(initialState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            array.generalInitialization(dimension = dimension)(initialState = initialState)

          def spawnCells(nCells: Int*)(states: State*): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            array.generalMultipleSpawn(dimension)(nCells*)(states*)

          def spawnCell(initialState: State)(spawnState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            array.generalSpawn(dimension)(initialState)(spawnState)

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
          def initializeSpace(state: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            array.generalInitialization(dimension)(state)
          def spawnCells(nCells: Int*)(states: State*): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            array.generalMultipleSpawn(dimension)(nCells*)(states*)
          def spawnCell(initialState: State)(spawnState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
            array.generalSpawn(dimension)(initialState)(spawnState)

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
    