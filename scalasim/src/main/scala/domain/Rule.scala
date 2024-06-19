package domain

import domain.Dimensions.Dimension
import domain.Dimensions.TwoDimensionalSpace
import domain.Position
import domain.Cell
import domain.Neighbor
import domain.CellularAutomata.State

trait Rule[I, O]:
   def tFunc(in: I): O
   def applyTransformation(ca: I): O = tFunc(ca)

trait NeighborRule[D <: Dimension] extends Rule[Neighbor[D], Cell[D]]

object NeighborRuleUtility:
   enum RelativePositions(x: Int, y: Int):
      case TopLeft      extends RelativePositions(-1, -1)
      case TopCenter    extends RelativePositions(-1, 0)
      case TopRight     extends RelativePositions(-1, 1)
      case CenterLeft   extends RelativePositions(0, -1)
      case Center       extends RelativePositions(0, 0)
      case CenterRight  extends RelativePositions(0, 1)
      case BottomLeft   extends RelativePositions(1, -1)
      case BottomCenter extends RelativePositions(1, 0)
      case BottomRight  extends RelativePositions(1, 1)

      def coordinates: List[Int] = List(x, y)
      
      
   trait NeighborhoodLocator[D <: Dimension]:
      def relativeNeighborsLocations: Iterable[Position[D]]
      def absoluteNeighborsLocations(center: Position[D]): Iterable[Position[? <: Dimension]] =
         relativeNeighborsLocations.map(c =>
            Position((center.coordinates zip c.coordinates) map { case (a, b) => a + b})
         ).filter(p => !p.coordinates.toList.contains((x: Int) => x < 0))

   case class CircleNeighborhoodLocator() extends NeighborhoodLocator[TwoDimensionalSpace]:
      override def relativeNeighborsLocations: Iterable[Position[TwoDimensionalSpace]] =
         import RelativePositions.*
         List(
            TopLeft,
            TopCenter,
            TopRight,
            CenterLeft,
            CenterRight,
            BottomLeft,
            BottomCenter,
            BottomRight,
         ).map(p => Position(p.coordinates))

   def getNeighboursWithState[D <: Dimension](state: State, neighbours: Neighbor[D]): List[Cell[D]] = ???
