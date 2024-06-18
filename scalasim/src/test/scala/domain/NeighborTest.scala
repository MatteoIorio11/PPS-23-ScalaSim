package domain

import org.scalatest.matchers.should.Matchers.*
import domain.NeighborRuleUtility.NeighborhoodLocator
import domain.Dimensions.TwoDimensionalSpace
import domain.Position.Position2D

class NeighborTest extends org.scalatest.funsuite.AnyFunSuite:
    test("Neighborhoodlocator should work as expected"):
      val neig = List((-1, -1), (1, 1)) map (c => Position2D(c.toList))
      val nLocator = new NeighborhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighborsLocations: Iterable[Position2D] = neig 

      val center = Position2D(List(0, 0))

      nLocator.absoluteNeighborsLocations(center) shouldBe neig.toIterable
