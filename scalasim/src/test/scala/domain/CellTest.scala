package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.Cell.*
import domain.CellularAutomata.*
import domain.Position.Position2D

class CellTest extends AnyFunSuite:
    enum MyState extends State:
        case TEST

    test("Creation of a new Cell with null elements should return an error"):
        val error = intercept[IllegalArgumentException](Cell2D(null, null))
        error shouldBe a[IllegalArgumentException]

    test("Creation of a Cell with dimension equals to null and correct state should return an error"):
        val state: State = MyState.TEST
        val error = intercept[IllegalArgumentException](Cell2D(null, state))
  
