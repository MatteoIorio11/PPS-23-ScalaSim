package domain

import domain.Dimensions.*

trait Position[D <: Dimension]:
  def coordinates: Tuple
  def asPosition[P <: Position[?]]: P = this.asInstanceOf[P]

object Position:
  def apply(coordinates: Tuple): Position[? <: Dimension] = coordinates match
    case (x: Int, y: Int) => Position2D((x, y))
    case (x: Int, y: Int, z: Int) => Position3D((x, y, z))
    case _ => throw new NotImplementedError(s"Position${coordinates.size}D is not yet implemented")

  class Position2D(private val _coordinates: (Int, Int)) extends Position[TwoDimensionalSpace]:
    override def coordinates: (Int, Int) = _coordinates

  class Position3D(private val _coordinates: (Int, Int, Int)) extends Position[ThreeDimensionalSpace]:
    override def coordinates: (Int, Int, Int) = _coordinates
