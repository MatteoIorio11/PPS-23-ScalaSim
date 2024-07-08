package domain

import org.scalatest.matchers.should.Matchers.*
import domain.automaton.NeighborRuleUtility.NeighbourhoodLocator
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Position
import domain.automaton.NeighborRuleUtility.getCircularNeighbourhoodPositions
import domain.automaton.CellularAutomaton.ValuedState

class NeighborTest extends org.scalatest.funsuite.AnyFunSuite:
    test("A two dimensional neighborhood should be mapped correctly"):
      val c0 = Cell[TwoDimensionalSpace](Position(0, 0), new State {})
      val others: List[Cell[TwoDimensionalSpace]] = List(
        Cell(Position(0, 1), new State {}),
        Cell(Position(1, 0), new State {}),
        Cell(Position(1, 1), new State {}),
      )

      val n = Neighbour(c0, others)

      n.center shouldBe c0
      n.neighbourhood shouldBe others

    test("A Neighborhood Locator should behave as expected"):
      val positions: List[Position[TwoDimensionalSpace]] = List(Position(0, -1), Position(0, 1))
      val horizontalNL = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] = positions

      horizontalNL.relativeNeighboursLocations shouldBe positions
      val expectedPositions: List[Position[TwoDimensionalSpace]] = List(Position(0, 0), Position(0, 2))
      val center: Position[TwoDimensionalSpace] = Position(0, 1)
      horizontalNL.absoluteNeighboursLocations(center) should contain theSameElementsAs expectedPositions

    test("A Neighborhood Locator should map a correct neighborhood"):
      val c0: Cell[TwoDimensionalSpace] = Cell[TwoDimensionalSpace](Position(1, 1), new State {})
      val c1: Position[TwoDimensionalSpace] = Position(0, 0)
      val c2: Position[TwoDimensionalSpace] = Position(2, 2)

      val diagonalNeighbourhoodLocator = new NeighbourhoodLocator[TwoDimensionalSpace]:
        override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
           List(Position(-1, -1), Position(1, 1))
          
      diagonalNeighbourhoodLocator.absoluteNeighboursLocations(c0.position) shouldBe List(c1, c2)

    test("Neighbourhood locator with circular neighbourhood positions should work as expected"):
      val center: Position[TwoDimensionalSpace] = Position(1, 1)

      val absNeighbourhood: List[Position[TwoDimensionalSpace]] = List(
        (0, 0), (0, 1), (0, 2),
        (1, 0),         (1, 2),
        (2, 0), (2, 1), (2, 2)
      ).map(x => Position(x._1, x._2))

      val relativeNeighbourhood: List[Position[TwoDimensionalSpace]] = List(
        (-1, -1), (-1, 0), (-1, 1),
        (0, -1),         (0, 1),
        (1, -1), (1, 0), (1, 1)
      ).map(x => Position(x._1, x._2))

      val locator = getCircularNeighbourhoodPositions(radius = 1)
      locator.relativeNeighboursLocations.toList should contain theSameElementsAs relativeNeighbourhood
      locator.absoluteNeighboursLocations(center) should contain theSameElementsAs absNeighbourhood


    test("Neighbourhood locator with circular neighbourhood positions for radius 2 should work as expected"):
      val center: Position[TwoDimensionalSpace] = Position(2, 2)

      val absNeighbourhood: List[Position[TwoDimensionalSpace]] = List(
        (0, 0), (0, 1), (0, 2), (0, 3), (0, 4),
        (1, 0), (1, 1), (1, 2), (1, 3), (1, 4),
        (2, 0), (2, 1),         (2, 3), (2, 4),
        (3, 0), (3, 1), (3, 2), (3, 3), (3, 4),
        (4, 0), (4, 1), (4, 2), (4, 3), (4, 4),
      ).map(x => Position(x._1, x._2))

      val relativeNeighbourhood: List[Position[TwoDimensionalSpace]] = List(
        (-2, -2), (-2, -1), (-2, 0), (-2, 1), (-2, 2),
        (-1, -2), (-1, -1), (-1, 0), (-1, 1), (-1, 2),
        (0, -2),  (0, -1),           (0, 1),  (0, 2),
        (1, -2),  (1, -1),  (1, 0),  (1, 1),  (1, 2),
        (2, -2),  (2, -1),  (2, 0),  (2, 1),  (2, 2)
      ).map(x => Position(x._1, x._2))

      val locator = getCircularNeighbourhoodPositions(radius = 2)

      // locator.relativeNeighboursLocations.toList should contain theSameElementsAs relativeNeighbourhood
      locator.absoluteNeighboursLocations(center) should contain theSameElementsAs absNeighbourhood