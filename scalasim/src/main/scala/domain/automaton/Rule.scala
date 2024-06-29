package domain.automaton

import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.automaton.Cell
import domain.automaton.Neighbour
import CellularAutomaton.State

trait Rule[I, O]:
   def tFunc(in: I): O
   def applyTransformation(ca: I): O = tFunc(ca)

trait NeighbourRule[D <: Dimension] extends Rule[Neighbour[D], Cell[D]]

object NeighborRuleUtility:
   private enum RelativePositions(x: Int, y: Int):
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
      
   trait NeighbourhoodLocator[D <: Dimension]:
      def relativeNeighboursLocations: Iterable[Position[D]]

      def absoluteNeighboursLocations(center: Position[D]): Iterable[Position[D]] =
         relativeNeighboursLocations.map(c =>
            Position(((center.coordinates zip c.coordinates) map { case (a, b) => a + b}).toArray*)
         ).filter(p => !p.coordinates.toList.exists(_ < 0))

   given circleNeighbourhoodLocator: NeighbourhoodLocator[TwoDimensionalSpace] = new NeighbourhoodLocator[TwoDimensionalSpace]:
      override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
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
         ).map(p => Position(p.coordinates*))

   def getNeighboursWithState[D <: Dimension](state: State, neighbours: Neighbour[D]): List[Cell[D]] = 
      neighbours.neighbourhood.filter(cell => cell.state == state)
