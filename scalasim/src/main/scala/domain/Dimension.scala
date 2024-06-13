package domain

import domain.Neighbor.Position

object Dimensions:
  trait Dimension:
    def dimensions: Int

  case class TwoDimensionalSpace() extends Dimension:
    override val dimensions: Int = 2
    def euckideanDistance(p1: Position[TwoDimensionalSpace], p2: Position[TwoDimensionalSpace]): Int

  case class ThreeDimensionalSpace() extends Dimension:
    override val dimensions: Int = 3