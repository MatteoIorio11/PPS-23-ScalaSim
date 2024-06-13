package domain

import org.scalatest.matchers.should.Matchers.*

import domain.Neighbor.*

class PositionTest extends org.scalatest.funsuite.AnyFunSuite:

  test("Two dimensional position should be created properly"):
    val p: Position2D = Position((10, 20, 30))