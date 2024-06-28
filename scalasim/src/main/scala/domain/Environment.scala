package domain

import scala.collection.mutable.ArrayBuffer
import domain.base.Dimensions.*
import domain.automaton.CellularAutomaton.*
import domain.automaton.Cell.*
import scala.util.Random
import domain.base.Position.Position2D
import automaton.Cell
import base.Position
import automaton.Neighbour

object Environment:
    /**
      * An Environment has inside It a Cellular Automaton [[CellularAutomaton]] and also a Matrix [[Matrix]],
      * that can be defined when this trait is implemented, by doing this the user is able to decide which
      * logic to use for the matrix abstraction. The Environment is responsible for managing all the cell [[Cell]]
      * that are stored inside the matrix, and for each cell, the Environment apply the right Cellular Automaton rule,
      * based on the input cell and all Its neighbours [[Iterable[Cell[D]]]].
      */
    trait Environment[D <: Dimension]:
        type Matrix
        def currentMatrix: Matrix
        def cellularAutomata: CellularAutomaton[D]
        def matrix: Matrix
        def neighbours(cell: Cell[D]): Iterable[Cell[D]]
        protected def saveCell(cell: Cell[D]): Unit 
        def applyRule(cell: Cell[D], neighbors: Iterable[Cell[D]]): Cell[D] = 
            val neighbour = Neighbour(cell, neighbors)
            val newCell = cellularAutomata.applyRule(cell, neighbour)
            saveCell(newCell)
            newCell
        protected def initialise(): Unit
        protected def availableCells(positions: Iterable[Position[D]]): Iterable[Cell[D]]
    /**
      * This trait represent a Square Environment 2D.
      */
    trait SquareEnvironment extends Environment[TwoDimensionalSpace]:
        def side: Int
    /**
      * This trait represent a Cubic Environment 3D. 
      */
    trait CubicEnvironment extends Environment[ThreeDimensionalSpace]:
        def edge: Int
    /**
      * This trait represent a Rectangular Environment 2D.
      */
    trait RectangularEnvironment extends Environment[TwoDimensionalSpace]:
        def width: Int
        def heigth: Int
    /**
      * Environment 2D, where the matrix is defined as an [[ArrayBuffer(ArrayBuffer)]]. This type of matrix can be 
      * very efficient because it allows us to have an O(1) random time access.
    */
    trait ArrayEnvironment2D extends Environment[TwoDimensionalSpace]:
        override type Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]
        override protected def saveCell(cell: Cell[TwoDimensionalSpace]): Unit = 
            val x = cell.position.coordinates.head
            val y = cell.position.coordinates.last
            matrix(x)(y) = cell
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
                matrix.map(row => row.map(cell => Cell(Position(cell.position.coordinates), cell.state)))
    /**
      * This trait represent a Toroid Environment 2D, where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait ToroidEnviroenment extends RectangularEnvironment with ArrayEnvironment2D:
        val MAX_SIZE = 2 // width and height
        override protected def saveCell(cell: Cell[TwoDimensionalSpace]) =  
            val x = cell.position.coordinates.head
            val y = cell.position.coordinates.last
            matrix(x % heigth)(y % width) = cell
        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]) = 
            positions.map(pos => {
                pos.coordinates match
                    case head :: next :: tail => List(head % heigth, next % width)
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
                val array = ArrayBuffer.fill(width, heigth)(initialCell)
                for (y <- 0 until width)
                    for (x <- 0 until heigth)
                        array(x)(y) = (Cell(Position2D((x, y).toList), initialCell.state))
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
                    val position = Position2D((x, y).toList)
                    if (array(x)(y).state != state)
                        array(x)(y) = (Cell(position, state))
                        spawnedCells = spawnedCells + 1
                array
            /**
              * This extension method returns a new Matrix in which there can be X cells with the inputState
              * otherwise the cell has It's initial state. The selected state is choosen by using a random value
              * where if the random value is True then It is selected the inputState otherwise the original state.
              * @param inputState: Input state to use If the probability returned from the random value is True
              * @return a new Matrix in which there can be spawned a number of cell with the input state.
              */
            def spawnCell(inputState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
              val initialCell: Cell[TwoDimensionalSpace] = Cell(Position((-1, -1).toList), inputState)
              val array = ArrayBuffer.fill(width, heigth)(initialCell)
              for (y <- 0 until heigth)
                for (x <- 0 until width)
                    val probability = Random().nextBoolean()
                    val state = probability match
                        case x if x => inputState
                        case _ => array(x)(y).state
                    array(x)(y) = Cell(Position((x, y).toList), state)
              array(0)(0) = Cell(Position((0, 0).toList), inputState)
              array

    /**
      * Square Environment 2D, where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait SquareArrayEnvironment2D extends SquareEnvironment with ArrayEnvironment2D:
        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] =
          positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < side))
            .map(pos => pos.coordinates.toList)
            .map(cor => matrix(cor.head)(cor.last))
        /**
          * Utilities methods.
          */
        extension (array: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]])
            /**
              * This extension method can be used for initialize the entire matrix with an initial type of cell,
              * every cell will be placed inside the right coordinates and every cell will have the correct position.
              * @param initialCell: prototype of the cell.
              * @return a new Matrix filled with cells.
              */
            def initializeSpace(initialCell: Cell[TwoDimensionalSpace]): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
                val array = ArrayBuffer.fill(side, side)(initialCell)
                  for (y <- 0 until side)
                    for (x <- 0 until side)
                      array(x)(y) = (Cell(Position2D((x, y).toList), initialCell.state))
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
                    val position = Position2D((x, y).toList)
                    if (array(x)(y).state != state)
                        array(x)(y) = (Cell(position, state))
                        spawnedCells = spawnedCells + 1
                array
            /**
              * This extension method can be used for spawn a random number of cell inside the Matrix with the input state.
              * @param inputState: state that will be used iniside the spawned cells.
              * @return a new Matrix where there are a number of random cells with the input state.
              */
            def spawnCell(inputState: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] = 
              val initialCell: Cell[TwoDimensionalSpace] = Cell(Position((-1, -1).toList), inputState)
              val array = ArrayBuffer.fill(side, side)(initialCell)
              for (y <- 0 until side)
                for (x <- 0 until side)
                    val probability = Random().nextBoolean()
                    val state = probability match
                        case x if x => inputState
                        case _ => array(x)(y).state
                    array(x)(y) = Cell(Position((x, y).toList), state)
              array(0)(0) = Cell(Position((0, 0).toList), inputState)
              array
    /**
      * Rectangula Environment2D where the matrix is defined using the [[ArrayEnvironment2D]] trait.
      */
    trait RectangularArrayEnvironment2D extends RectangularEnvironment with ArrayEnvironment2D:
        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]) = 
            positions
              .filter(pos => pos.coordinates.size == 2)
              .filter(pos => pos.coordinates.head >= 0 && pos.coordinates.last < heigth &&
                             pos.coordinates.last >= 0 && pos.coordinates.last < width)
              .map(pos => matrix(pos.coordinates.head)(pos.coordinates.last))