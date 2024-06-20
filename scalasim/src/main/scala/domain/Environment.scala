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
    trait Environment[D <: Dimension, I, O]:
        type Matrix
        def matrix: Matrix
        def dimension: Int
        def cellularAutomata: CellularAutomata[D, I, O]
        def neighboors(cell: Cell[D]): List[Cell[D]]
        protected def initialise(): Unit
        protected def availableCells(positions: List[Position[D]]): List[Cell[D]]
    

