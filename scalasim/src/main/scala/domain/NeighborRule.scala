package domain

import domain.Dimensions.*

object Neighbor:
  trait Position[D <: Dimension]:
    def coordinates: Tuple

  object Position:
    def apply(d: Dimension) = d.dimensions match
      case 2 => ???
      case 3 => ???
      case _ => ???
    
    class Position2D(private val _coordinates: Tuple2[Int, Int]) extends Position[TwoDimensionalSpace]:
      def coordinates: Tuple2[Int, Int] = ???


  trait NeighborRule[D <: Dimension]:
    def listR: List[Tuple]