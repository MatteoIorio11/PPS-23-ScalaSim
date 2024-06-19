package domain

import org.scalatest.matchers.should.Matchers.*
import domain.NeighborRuleUtility.NeighborhoodLocator
import domain.Dimensions.TwoDimensionalSpace
import domain.Position.Position2D
import domain.Neighbor
import domain.Cell
import domain.CellularAutomata.State

class NeighborTest extends org.scalatest.funsuite.AnyFunSuite:
    test("Neighborhoodlocator should work as expected"):
      val neig = List((-1, -1), (1, 1)) map (c => Position2D(c.toList))
      val nLocator = new NeighborhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighborsLocations: Iterable[Position2D] = neig 

      val center = Position2D(List(0, 0))

      nLocator.absoluteNeighborsLocations(center) shouldBe neig.toIterable

    test("A two dimensional neighborhood should be mapped correctly"):
      val c0 = Cell(Position2D((0, 0).toList), new State {})
      val others = List(
        Cell(Position2D((0, 1).toList), new State {}),
        Cell(Position2D((1, 0).toList), new State {}),
        Cell(Position2D((1, 1).toList), new State {}),
      ).toIterable

      val n = Neighbor(c0, others)

      n.center shouldBe c0
      n.neighborhood shouldBe others

