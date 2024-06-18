package domain

import domain.Dimensions.Dimension
import domain.Dimensions.TwoDimensionalSpace
import domain.Position
import domain.Cell.Cell
import domain.Neighbor
import domain.CellularAutomata.State

trait Rule[I, O]:
   def tFunc(in: I): O
   def applyTransformation(ca: I): O = tFunc(ca)

trait NeighborRule[D <: Dimension] extends Rule[Neighbor[D], Cell[D]]

object NeighborRuleUtility:
   def getNeighboursWithState[D <: Dimension](state: State, neighbours: Neighbor[D]): List[Cell[D]] = ???
      