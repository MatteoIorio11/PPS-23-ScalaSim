package domain

import org.scalatest.matchers.should.Matchers.*
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position.Position2D
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Position
import domain.automaton.NeighborRuleUtility.getCircularNeighbourhoodPositions

class NeighborTest extends org.scalatest.funsuite.AnyFunSuite:
    test("Neighborhoodlocator should work as expected"):
      val neig = List((-1, -1), (1, 1)) map (c => Position2D(c.toList))
      val nLocator = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position2D] = neig

      val center = Position2D(List(0, 0))

      nLocator.absoluteNeighboursLocations(center) shouldBe neig.toIterable

    test("A two dimensional neighborhood should be mapped correctly"):
      val c0 = Cell(Position2D((0, 0).toList), new State {})
      val others = List(
        Cell(Position2D((0, 1).toList), new State {}),
        Cell(Position2D((1, 0).toList), new State {}),
        Cell(Position2D((1, 1).toList), new State {}),
      ).toIterable

      val n = Neighbour(c0, others)

      n.center shouldBe c0
      n.neighbourhood shouldBe others

    test("A Neighborhood Locator should behave as expected"):
      val positions = List((0, -1), (0, 1)) map (c => Position2D(c.toList))
      val horizontalNL = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] = positions


      horizontalNL.relativeNeighboursLocations shouldBe positions.toIterable
      val expectedPositions = List((0, 0), (0, 2)) map (c => Position2D(c.toList))
      val center = Position2D((0, 1).toList)
      horizontalNL.absoluteNeighboursLocations(center) shouldBe expectedPositions.toIterable

    test("A Neighborhood Locator should map a correct neighborhood"):
      val c0 = Cell(Position2D((1, 1).toList), new State {})
      val c1 = Position2D((0, 0).toList)
      val c2 = Position2D((2, 2).toList)

      val diagonalNeighbourhoodLocator = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
           List((-1, -1), (1, 1)).map(c => Position2D(c.toList))
          
      diagonalNeighbourhoodLocator.absoluteNeighboursLocations(c0.position) shouldBe List(c1, c2).toIterable

    test("Neighbourhood locator with circular neighbourhood positions should work as expected"):
      val center: Position[TwoDimensionalSpace] = Position(List(1, 1))

      val absNeighbourhood: List[Position[TwoDimensionalSpace]] = List(
        (0, 0), (0, 1), (0, 2),
        (1, 0),         (1, 2),
        (2, 0), (2, 1), (2, 2)
      ).map(x => Position(x.toList))

      val relativeNeighbourhood: List[Position[TwoDimensionalSpace]] = List(
        (-1, -1), (-1, 0), (-1, 1),
        (0, -1),         (0, 1),
        (1, -1), (1, 0), (1, 1)
      ).map(x => Position(x.toList))

      val locator = getCircularNeighbourhoodPositions(radius = 1)
      locator.relativeNeighboursLocations.toList should contain theSameElementsAs relativeNeighbourhood
      locator.absoluteNeighboursLocations(center) should contain theSameElementsAs absNeighbourhood