package domain

import org.scalatest.matchers.should.Matchers.*

import domain.Neighbor.Position.Position2D
import domain.Neighbor.Position

class PositionTest extends org.scalatest.funsuite.AnyFunSuite:

  test("Two dimensional position should be created properly"):
    val p: Position2D = Position2D((10, 20))
    p.coordinates shouldBe (10, 20)

