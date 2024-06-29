package domain

import org.scalatest.matchers.should.Matchers.*

import domain.base.Position
import domain.base.Position.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Dimensions.ThreeDimensionalSpace

class PositionTest extends org.scalatest.funsuite.AnyFunSuite:

  test("Two dimensional position should be created properly"):
    val p: Position[TwoDimensionalSpace] = Position(10, 20)
    p.coordinates shouldBe List(10, 20).toIterable

  test("Three dimensional position should be created properly"):
    val p: Position[ThreeDimensionalSpace] = Position(10, 20, 30)
    p.coordinates shouldBe List(10, 20, 30).toIterable

  test("Generic position construction with two coordinates should map a Postion2D"):
    val p = Position(10, 20)
    p.coordinates shouldBe List(10, 20).toIterable
    p.isInstanceOf[Position2D] shouldBe true

  test("Generic position construction with three coordinates should map a Postion3D"):
    val p = Position(10, 20, 30)
    p.coordinates shouldBe List(10, 20, 30).toIterable
    p.isInstanceOf[Position3D] shouldBe true

  test("Casting with `asPosition` should work as expected"):
    val p1: Position2D = Position(1, 2).asPosition[Position2D]
    val p2: Position3D = Position(1, 2, 3).asPosition[Position3D]

