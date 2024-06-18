package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.Cell.*
import domain.CellularAutomata.*
import domain.Position.Position2D

class Cell2DTest extends AnyFunSuite:
    enum MyState extends State:
        case TEST

    test("Creation of a new Cell with null elements should return an error"):
        val error = intercept[IllegalArgumentException](Cell2D(null, null))
        error shouldBe a[IllegalArgumentException]

    test("Creation of a Cell with dimension equals to null and correct state should return an error"):
        val state: State = MyState.TEST
        val error = intercept[IllegalArgumentException](Cell2D(null, state))
    
    test("Creation of a Cell with dimension and state not null should be ok"):
        val state: State = MyState.TEST
        val position: Position2D = Position2D(0, 0)
        val cell = Cell2D(position, state)
  
