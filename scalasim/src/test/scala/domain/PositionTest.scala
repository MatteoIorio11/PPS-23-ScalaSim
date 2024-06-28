package domain

import org.scalatest.matchers.should.Matchers.*

import domain.base.Position
import domain.base.Position.*
import domain.base.Dimensions.TwoDimensionalSpace

class PositionTest extends org.scalatest.funsuite.AnyFunSuite:

  test("Two dimensional position should be created properly"):
    val p: Position2D = Position2D((10, 20).toList)
    p.coordinates shouldBe List(10, 20).toIterable

  test("Three dimensional position should be created properly"):
    val p: Position3D = Position3D((10, 20, 30).toList)
    p.coordinates shouldBe List(10, 20, 30).toIterable

  test("Generic position construction with two coordinates should map a Postion2D"):
    val p = Position((10, 20).toList)
    p.coordinates shouldBe List(10, 20).toIterable
    p.isInstanceOf[Position2D] shouldBe true

  test("Generic position construction with three coordinates should map a Postion3D"):
    val p = Position((10, 20, 30).toList)
    p.coordinates shouldBe List(10, 20, 30).toIterable
    p.isInstanceOf[Position3D] shouldBe true

  test("Casting with `asPosition` should work as expected"):
    val p1: Position2D = Position((1, 2).toList).asPosition[Position2D]
    val p2: Position3D = Position((1, 2, 3).toList).asPosition[Position3D]

  test("Position arithmetic operators shoudl work as expected"):
    import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*

    Position(List(10, 20)) - 10 shouldBe Position(List(0, 10))
    Position(List(10, 20)) - Position(List(10, 20)) shouldBe Position(List(0, 0))
    Position(List(10, 20)) + Position(List(-1, -1)) shouldBe Position(List(9, 19))
