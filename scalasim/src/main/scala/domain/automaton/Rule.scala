package domain.automaton

import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.automaton.Cell
import domain.automaton.Neighbour
import CellularAutomaton.State
import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*

trait Rule[I, O]:
   def tFunc(in: I): O
   def applyTransformation(ca: I): O = tFunc(ca)

/**
  * A Neighbor rule is a [[Rule]] based on neighbors' states of a given
  * cell. Transformation funrctions of this rules should map
  * a D dimensional [[Neighbour]] into a D dimensional [[Cell]]
  * 
  * @param D the dimension of the space.
  */
trait NeighbourRule[D <: Dimension] extends Rule[Neighbour[D], Cell[D]]

object NeighborRuleUtility:
   enum RelativePositions(x: Int, y: Int):
      case TopLeft(n: Int = 0)      extends RelativePositions(n * -1, n * -1)
      case TopCenter(n: Int = 0)    extends RelativePositions(n * -1, n * 0)
      case TopRight(n: Int = 0)     extends RelativePositions(n * -1, n * 1)
      case CenterLeft(n: Int = 0)   extends RelativePositions(n * 0, n * -1)
      case Center(n: Int = 0)       extends RelativePositions(n * 0, n * 0)
      case CenterRight(n: Int = 0)  extends RelativePositions(n * 0, n * 1)
      case BottomLeft(n: Int = 0)   extends RelativePositions(n * 1, n * -1)
      case BottomCenter(n: Int = 0) extends RelativePositions(n * 1, n * 0)
      case BottomRight(n: Int = 0)  extends RelativePositions(n * 1, n * 1)

      def coordinates: List[Int] = List(x, y)

      def toPosition = Position(coordinates)

   trait NeighbourhoodLocator[D <: Dimension]:
      def relativeNeighboursLocations: Iterable[Position[D]]

      def absoluteNeighboursLocations(center: Position[D]): Iterable[Position[D]] =
         relativeNeighboursLocations.map(c =>
            center + c
         ).filter(p => !p.coordinates.toList.contains((x: Int) => x < 0))

   object PositionArithmeticOperations:
      extension[D <: Dimension] (p: Position[D])
         private def elementWiseFunc(other: Position[D])(func: (Int, Int) => Int): Position[D] =
            Position((p.coordinates zip other.coordinates) map { case (a, b) => func(a, b)})
         def +(other: Position[D]): Position[D] = elementWiseFunc(other)(_ + _)
         def -(other: Position[D]): Position[D] = elementWiseFunc(other)(_ - _)
         def -(n: Int): Position[D] = Position(p.coordinates.map(_ - n))

   given circleNeighbourhoodLocator: NeighbourhoodLocator[TwoDimensionalSpace] = new NeighbourhoodLocator[TwoDimensionalSpace]:
      override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
         import RelativePositions.*
         List(
            TopLeft(),
            TopCenter(),
            TopRight(),
            CenterLeft(),
            CenterRight(),
            BottomLeft(),
            BottomCenter(),
            BottomRight(),
            ).map(p => Position(p.coordinates))

   def getCircularNeighbourhoodPositions(radius: Int = 1): NeighbourhoodLocator[TwoDimensionalSpace] =
      val center = Position(List(radius, radius))
      var neighbours: List[Position[TwoDimensionalSpace]] = List.empty
      for
         i <- (0 to radius + 1)
         j <- (0 to radius + 1)
      do neighbours = neighbours :+ Position(List(i, j))
      new NeighbourhoodLocator[TwoDimensionalSpace]:
         override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
            neighbours.filter(_ != center) map (_ - radius)

   def getNeighboursWithState[D <: Dimension](state: State, neighbours: Neighbour[D]): List[Cell[D]] = 
      neighbours.neighbourhood.filter(cell => cell.state == state)
