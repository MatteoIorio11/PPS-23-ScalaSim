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
   trait NeighborhoodLocator[D <: Dimension]:
      def relativeNeighborsLocations: Iterable[Position[D]]
      def absoluteNeighborsLocations(center: Position[D]): Iterable[Position[? <: Dimension]] =
         relativeNeighborsLocations.map(c =>
            Position((center.coordinates zip c.coordinates) map { case (a, b) => a + b})
         )

   def getNeighboursWithState[D <: Dimension](state: State, neighbours: Neighbor[D]): List[Cell[D]] = ???