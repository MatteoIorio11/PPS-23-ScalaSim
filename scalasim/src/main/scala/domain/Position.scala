package domain

import domain.Dimensions.*

/**
 * A generic position in space.
 *
 * @param D the [[Dimension]] of the space.
 */
trait Position[D <: Dimension]:
  def coordinates: Iterable[Int]
  def asPosition[P <: Position[?]]: P = this.asInstanceOf[P]

object Position:
  /**
   * Creates a generic position, mapped in the specific implementation of a [[D]] dimensional
   * position.
   *
   * @param coordinates a collection (iterable) of integers representing spatial cooridinates.
   */
  def apply[D <: Dimension](coordinates: Iterable[Int]): Position[D] = coordinates.toList match
    case x if x.size == 2 => Position2D(x).asInstanceOf[Position[D]]
    case x if x.size == 3 => Position3D(x).asInstanceOf[Position[D]]
    case _ => throw new NotImplementedError(s"Position${coordinates.size}D is not yet implemented")

  case class Position2D(val coordinates: Iterable[Int]) extends Position[TwoDimensionalSpace]

  case class Position3D(val coordinates: Iterable[Int]) extends Position[ThreeDimensionalSpace]
