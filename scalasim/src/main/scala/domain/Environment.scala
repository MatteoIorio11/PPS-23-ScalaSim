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
    type Matrix
    trait Environment[D <: Dimension, I, O]:
        type Matrix
        def matrix: Matrix
        def dimension: Int
        def cellularAutomata: CellularAutomata[D, I, O]
        def neighbours(cell: Cell[D]): Iterable[Cell[D]]
        protected def initialise(): Unit
        protected def availableCells(positions: Iterable[Position[D]]): Iterable[Cell[D]]
    

    trait ArrayEnvironment2D[D <: Dimension, I, O] extends Environment[D, I, O]:
        override type Matrix = Array[Array[Cell[D]]]
        