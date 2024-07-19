package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.simulations.gameoflife.GameOfLife.*
import domain.simulations.gameoflife.*
import org.scalatest.BeforeAndAfterEach
import domain.base.Dimensions.TwoDimensionalSpace
import automaton.Cell
import base.Position
import scala.collection.mutable.ArrayBuffer

class GameOfLifeEnvironmentTest extends AnyFunSuite with BeforeAndAfterEach:
    val (width, height)= (100, 100)
    val env = GameOfLifeEnvironment(height, width, Map(
        CellState.ALIVE -> width*height/3
    ))

    test("Initialise environment with dimension <= 0 should throw an error"):
        val exception = intercept[RuntimeException](GameOfLifeEnvironment(0,0, Map()))
        exception shouldBe a[IllegalArgumentException]


    test("Initialize an environment should add cells into the matrix"):
        env.currentMatrix.length should not be 0
    
    test("Initialization should also add alive cells"):
        env.currentMatrix.filter(c => c.state == CellState.ALIVE).length should not be 0
    
    test("The neighboors of a cell should always exits"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), CellState.DEAD)
        env.neighbours(cell = cell) shouldNot be (List.empty)
