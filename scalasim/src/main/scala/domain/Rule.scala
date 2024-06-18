package domain

import domain.Dimensions.Dimension
import domain.Dimensions.TwoDimensionalSpace
import domain.Position
import domain.Cell.Cell
import domain.Neighbor

trait Rule[I, O]:
   def tFunc(in: I): O
   def applyTransformation(ca: I): O = tFunc(ca)

abstract class NeighborRule[D <: Dimension] extends Rule[Neighbor[D], Cell[D]]:
   def tFunc(in: Neighbor[D]): Cell[D]