package domain

import domain.Dimensions.*

object Neighbor:
  trait Position[D <: Dimension]:
    def coordinates: Tuple

  object Position:
    def apply(coordinates: Tuple): Position[? <: Dimension] = coordinates match
      case x: (Int, Int) => Position2D(x)
      case x: (Int, Int, Int) => Position3D(x)
      case _ => ???
    
    class Position2D(private val _coordinates: (Int, Int)) extends Position[TwoDimensionalSpace]:
      override def coordinates: (Int, Int) = _coordinates

    class Position3D(private val _coordinates: (Int, Int, Int)) extends Position[ThreeDimensionalSpace]:
      override def coordinates: (Int, Int, Int) = _coordinates


  trait NeighborRule[D <: Dimension]:
    def listR: List[Tuple]