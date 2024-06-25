package domain

import scala.collection.mutable.ArrayBuffer
import domain.base.Dimensions.*
import domain.automaton.CellularAutomaton.*
import domain.automaton.Cell.*
import scala.util.Random
import domain.base.Position.Position2D
import domain.simulations.gameoflife.GameOfLife.*
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import automaton.Cell
import base.Position
import automaton.Neighbour
import simulations.gameoflife.GameOfLife

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
        def dimension: Int
        def cellularAutomata: CellularAutomaton[D]
        def neighbours(cell: Cell[D]): Iterable[Cell[D]]
        protected def saveCell(cell: Cell[D]): Unit 
        def applyRule(cell: Cell[D], neighbors: Iterable[Cell[D]]): Cell[D] = 
            val neighbour = Neighbour(cell, neighbors)
            val newCell = cellularAutomata.applyRule(cell, neighbour)
            saveCell(newCell)
            newCell
        protected def initialise(): Unit
        protected def matrix: Matrix
        protected def availableCells(positions: Iterable[Position[D]]): Iterable[Cell[D]]
    

    /**
      * Environment 2D, where the matrix is defined as an ArrayBuffer.
      */
    trait ArrayEnvironment2D extends Environment[TwoDimensionalSpace]:
        override type Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]
        override protected def saveCell(cell: Cell[TwoDimensionalSpace]): Unit = 
            val x = cell.position.coordinates.head
            val y = cell.position.coordinates.last
            matrix.addCell(x, y)(dimension)(cell)

        override def currentMatrix: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
            matrix.clone()
        
        override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] =
          positions.filter(pos => pos.coordinates.forall(c => c >= 0 && c < dimension))
            .map(pos => pos.coordinates.toList)
            .map(cor => matrix(cor.head)(cor.last))
        
        extension (array: ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]])
          def initializeEmpty2D(dimension: Int)(initialCell: Cell[TwoDimensionalSpace]): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
              val array = ArrayBuffer.fill(dimension, dimension)(initialCell)
              for (y <- 0 until dimension)
                  for (x <- 0 until dimension)
                      array.addCell(x, y)(dimension)(Cell(Position2D((x, y).toList), CellState.DEAD))
              array

          def addCell(x: Int, y: Int)(dimension: Int)(cell: Cell[TwoDimensionalSpace]): Unit = 
            (x, y) match
              case (x, y) if x >= 0 && y >= 0 && x < dimension && y < dimension => array(x)(y) = cell
              case _ => throw new IllegalArgumentException("Index out of bound error")
            

          def initializeCells(nCells: Int, dimension: Int)(state: State): ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]] =
              var spawnedCells = 0
              while (spawnedCells < nCells)
                  val x = Random.nextInt(dimension)
                  val y = Random.nextInt(dimension)
                  val position = Position2D((x, y).toList)
                  if (array(x)(y).state != state)
                      array.addCell(x, y)(dimension)(Cell(position, state))
                      spawnedCells = spawnedCells + 1
              array