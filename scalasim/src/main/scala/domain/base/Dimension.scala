package domain.base

import domain.Position

object Dimensions:
  /**
   * Representation of dimensions of a given space (e.g. two/three dimensional)
   */
  trait Dimension:
    def dimensions: Int

  case class TwoDimensionalSpace() extends Dimension:
    override val dimensions: Int = 2

  case class ThreeDimensionalSpace() extends Dimension:
    override val dimensions: Int = 3
