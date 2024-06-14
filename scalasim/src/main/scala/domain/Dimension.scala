package domain

import domain.Position

object Dimensions:
  trait Dimension:
    def dimensions: Int

  case class TwoDimensionalSpace() extends Dimension:
    override val dimensions: Int = 2

  case class ThreeDimensionalSpace() extends Dimension:
    override val dimensions: Int = 3