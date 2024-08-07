package domain

import org.scalatest.matchers.should.Matchers.*

import domain.base.Position
import domain.base.Position.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Dimensions.ThreeDimensionalSpace

class PositionTest extends org.scalatest.funsuite.AnyFunSuite:

  test("Two dimensional position should be created properly"):
    val p: Position[TwoDimensionalSpace] = Position(10, 20)
    p.coordinates shouldBe List(10, 20)

  test("Three dimensional position should be created properly"):
    val p: Position[ThreeDimensionalSpace] = Position(10, 20, 30)
    p.coordinates shouldBe List(10, 20, 30)

  test("Generic position construction with two coordinates should map a Postion2D"):
    val p = Position(10, 20)
    p.coordinates shouldBe List(10, 20)
    p.isInstanceOf[Position2D] shouldBe true

  test("Generic position construction with three coordinates should map a Postion3D"):
    val p = Position(10, 20, 30)
    p.coordinates shouldBe List(10, 20, 30)
    p.isInstanceOf[Position3D] shouldBe true

  test("Casting with `asPosition` should work as expected"):
    val p1: Position2D = Position(1, 2).asPosition[Position2D]
    val p2: Position3D = Position(1, 2, 3).asPosition[Position3D]

  test("Position arithmetic operators shoudl work as expected"):
    import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*

    Position(10, 20) - 10 shouldBe Position(0, 10)
    Position(10, 20) - Position(10, 20) shouldBe Position(0, 0)
    Position(10, 20) + Position(-1, -1) shouldBe Position(9, 19)

  test("Position with more than three coordintes should not be yet implemented"):
    intercept[NotImplementedError]:
      Position(10, 20, 30, 40)

