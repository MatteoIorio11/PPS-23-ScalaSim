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
    

    trait ArrayEnvironment2D[D <: Dimension] extends Environment[D]:
        override type Matrix = ArrayBuffer[ArrayBuffer[Cell[D]]]
        override protected def saveCell(cell: Cell[D]): Unit = 
            val x = cell.position.coordinates.head
            val y = cell.position.coordinates.last
            matrix(x)(y) = cell
        