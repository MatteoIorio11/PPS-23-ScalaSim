package domain

import org.scalatest.matchers.should.Matchers.*

import domain.Neighbor.Position.*
import domain.Neighbor.Position
import domain.Dimensions.TwoDimensionalSpace

class PositionTest extends org.scalatest.funsuite.AnyFunSuite:

  test("Two dimensional position should be created properly"):
    val p: Position2D = Position2D((10, 20))
    p.coordinates shouldBe (10, 20)

  test("Three dimensional position should be created properly"):
    val p: Position3D = Position3D((10, 20, 30))
    p.coordinates shouldBe (10, 20, 30)

  test("Generic position construction with two coordinates should map a Postion2D"):
    val p = Position((10, 20))
    p.coordinates shouldBe (10, 20)
    p.isInstanceOf[Position2D] shouldBe true

  test("Generic position construction with three coordinates should map a Postion3D"):
    val p = Position((10, 20, 30))
    p.coordinates shouldBe (10, 20, 30)
    p.isInstanceOf[Position3D] shouldBe true

  test("Casting with `asPosition` should work as expected"):
    val p1: Position2D = Position((1, 2)).asPosition[Position2D]
    val p2: Position3D = Position((1, 2, 3)).asPosition[Position3D]

