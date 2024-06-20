package domain

import org.scalatest.matchers.should.Matchers.*
import domain.NeighborRuleUtility.NeighborhoodLocator
import domain.Dimensions.TwoDimensionalSpace
import domain.Position.Position2D
import domain.Neighbor
import domain.Cell
import domain.CellularAutomata.State
import domain.CellularAutomata.CellularAutomata

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

    test("A Neighborhood Locator should map a correct neighborhood"):
      val c0 = Cell(Position2D((1, 1).toList), new State {})

      val c1 = Position2D((0, 0).toList)
      val c2 = Position2D((2, 2).toList)

      val diagonalNeighbourhoodLocator = new NeighborhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighborsLocations: Iterable[Position[TwoDimensionalSpace]] =
           List((-1, -1), (1, 1)).map(c => Position2D(c.toList))
          
      diagonalNeighbourhoodLocator.absoluteNeighborsLocations(c0.position) shouldBe List(c1, c2).toIterable
        