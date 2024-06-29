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
      val neig: List[Position[TwoDimensionalSpace]] = List(Position(-1, -1), Position(1, 1))
      val nLocator = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] = neig

      val center: Position[TwoDimensionalSpace] = Position(0, 0)

      nLocator.absoluteNeighboursLocations(center) should contain theSameElementsAs neig.toIterable

    test("A two dimensional neighborhood should be mapped correctly"):
      val c0 = Cell[TwoDimensionalSpace](Position(0, 0), new State {})
      val others: List[Cell[TwoDimensionalSpace]] = List(
        Cell(Position(0, 1), new State {}),
        Cell(Position(1, 0), new State {}),
        Cell(Position(1, 1), new State {}),
      ).toIterable

      val n = Neighbour(c0, others)

      n.center shouldBe c0
      n.neighbourhood shouldBe others

    test("A Neighborhood Locator should behave as expected"):
      val positions: List[Position[TwoDimensionalSpace]] = List(Position(0, -1), Position(0, 1))
      val horizontalNL = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] = positions


      horizontalNL.relativeNeighboursLocations shouldBe positions.toIterable
      val expectedPositions: List[Position[TwoDimensionalSpace]] = List(Position(0, 0), Position(0, 2))
      val center: Position[TwoDimensionalSpace] = Position(0, 1)
      horizontalNL.absoluteNeighboursLocations(center) should contain theSameElementsAs expectedPositions.toIterable

    test("A Neighborhood Locator should map a correct neighborhood"):
      val c0: Cell[TwoDimensionalSpace] = Cell[TwoDimensionalSpace](Position(1, 1), new State {})
      val c1: Position[TwoDimensionalSpace] = Position(0, 0)
      val c2: Position[TwoDimensionalSpace] = Position(2, 2)

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
