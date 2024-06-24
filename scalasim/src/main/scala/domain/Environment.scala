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
        def matrix: Matrix
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
        protected def availableCells(positions: Iterable[Position[D]]): Iterable[Cell[D]]
    

    /**
      * Environment 2D, where the matrix is defined as an ArrayBuffer.
      */
    trait ArrayEnvironment2D[D <: Dimension] extends Environment[D]:
        override type Matrix = ArrayBuffer[ArrayBuffer[Cell[D]]]
        override protected def saveCell(cell: Cell[D]): Unit = 
            val x = cell.position.coordinates.head
            val y = cell.position.coordinates.last
            matrix(x)(y) = cell
        