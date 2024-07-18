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
      * @param D Cellular Automaton's space dimension.
      * @param R apply rule return type.
      */
    trait GenericEnvironment[D <: Dimension, R] extends Space[D]:
      protected def saveCell(cells: Cell[D]*): Unit
      /**
        * Execute a single step for the simulation, by going through all the cells stored inside the matrix and apply on each of them a specific rule.
        */
      def nextIteration: Unit
      /**
        * Apply a specific Cellular Automaton's rule by using the input cell and the neighbors. 
        * @param neighbors the neighbourhood to which apply a rule.
        * @return the result cell/cells after applying the rule.
        */
      protected def applyRule(neighbors: Neighbour[D]): R

    /**
      * This trait represents a particular type of Environment in which It is used a Cellular Automaton where
      * as return type for the Apply Rule method It has a single output Cell. This trait extends from the Generic Environment
      * by specifying in the generic value [[R]] the type [[Cell]].
      * @param D Cellular Automaton's space dimension.
      */
    trait SimpleEnvironment[D <: Dimension] extends GenericEnvironment[D, Cell[D]]:
      /**
        * Cellular Automaton that will be used inside this environment.
        */
      def cellularAutomata: CellularAutomaton[D]
      def applyRule(neighbors: Neighbour[D]): Cell[D] = 
        val newCell = cellularAutomata.applyRule(neighbors)
        saveCell(newCell)
        newCell

    /**
      * This trait represents a particular type of Environment in which It is used a Complex Cellular Automaton
      * where It returns after applying the rule an Iterable of Cell. This trait extends from the Generic Environment
      * by specifying in the generic type [[R]] the type [[Iterable]].
      */
    trait ComplexEnvironment[D <: Dimension] extends GenericEnvironment[D, Iterable[Cell[D]]]:
      /**
        * Cellular Automaton that will be used inside this environment.
        */
      def cellularAutomata: MultiOutputCellularAutomaton[D]
      override def applyRule(neighbors: Neighbour[D]): Iterable[Cell[D]] = 
          val newCell = cellularAutomata.applyRule(neighbors)
          saveCell(newCell.toSeq*)
          newCell
      
    /**
      * This trait It is used for representing the Space where all the cells will be stored during the simulation.
      * For this trait It is necessary to specify how to represent the Matrix by definying the type Matrix. This decision
      * allows the user to decied how to represent the space by using the best class for storing and modelling all the
      * cells.
      * @param D dimension in which the space is defined.
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
      def currentMatrix: LazyList[Cell[D]]
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
      def neighbours(cell: Cell[D]): Neighbour[TwoDimensionalSpace]
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
      * This trait represent a Square Environment 2D. A Square is defined by using one single component: the side.
      * By extending this trait It will be necessary to specify the Square's side. This informations
      * will be used for interact with the used Matrix.
      */
    trait SquareEnvironment extends Space[TwoDimensionalSpace]:
        def side: Int
        override def dimension: Tuple2[Int, Int] = (side, side)

    /**
      * This trait represent a Cubic Environment 3D. A Cubic, in solid geometry is defined by using one single component: the
      * edge. By extending this trait It will be necessary to specify the Cubic's edge. This informations
      * will be used for interact with the used Matrix.
      */
    trait CubicEnvironment extends Space[ThreeDimensionalSpace]:
        def edge: Int
        override def dimension = (edge, edge, edge)

   /**
      * This trait represent a Rectangular Environment 2D. A Rectangle is defined by two different component: the width and the heigth.
      * In fact by extending this method It will be necessary to specify the Rectangle's informations. This informations
      * will be used for interact with the used Matrix.
      */
    trait RectangularEnvironment extends Space[TwoDimensionalSpace]:
        def width: Int
        def heigth: Int
        override def dimension: Tuple2[Int, Int] = (heigth, width)

    /**
      * This trait represent a Toroid Environment 2D. A Toroid is a surface of revolution with a hole in the middle.
      * The axis of revolution passes through the hole and so does not intersect the surface. The Toroid can be represented in a 2D
      * space by using a more simpler gemoetry figure, the Rectangle.
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
    trait TrieMapEnvironment[D <: Dimension] extends Space[TwoDimensionalSpace]:
      override type Matrix = TrieMap[Position[D], Cell[D]]
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
      override def nextIteration: Unit = 
        matrix.flatMap(cells => cells.map(cell => cell))
          .foreach(cell => applyRule(neighbours(cell)))
      override def currentMatrix: LazyList[Cell[TwoDimensionalSpace]] = 
          LazyList(matrix.flatten.toSeq*)
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
            * This initial matrix by using the input initial state. This method is general and can be used for all type of geometry space.
            * @param dimension a tuple where It are defined all the space's dimension.
            * @param initialCell initial cell to use for initialize the space.
            * @return a new Matrix initialized with the input initial state.
            */
          protected def generalInitialization(dimension: Tuple2[Int, Int])(initialState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            val cell: Cell[TwoDimensionalSpace] = Cell(Position(-1, -1), initialState)
            val array = ArrayBuffer.fill(dimension._1, dimension._2)(cell)
            for (y <- 0 until dimension._2)
                for (x <- 0 until dimension._1)
                    array(x)(y) = (Cell(Position(x, y), initialState))
            array
          /**
            * This extension method returns a new Matrix in which there can be X cells with the spawnState
            * otherwise the cell has It's initial state. The selected state is choosen by using a random value
            * where if the random value is True then It is selected the inputState otherwise the original state.
            * @param dimension 
            * @param initialState state used for the matrix initialization.
            * @param spawnState state to use if in a specific cell is possible to use this state.
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
            * This extension method returns a new Matrix in which there are #cells for the specific input state.
            * @param dimension a tuple where It are defined all the space's dimension.
            * @param nCells: number of cells to spawn for each state, nCells[i] is linked to the states[i]
            * @param state: input states that will be spawned inside the matrix.
            * @return a new Matrix where are inserted inside the old matrix the new states.
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
      * Square Environment 2D, where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait SquareArrayEnvironment2D extends SquareEnvironment with ArrayEnvironment2D:
        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] =
          positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < side))
            .map(pos => pos.coordinates.toList)
            .filter(cor => cor.size == MAX_SIZE)
            .map(cor => matrix(cor.head)(cor.last))
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