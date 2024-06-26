package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.simulations.gameoflife.GameOfLife.*
import domain.simulations.gameoflife.*
import org.scalatest.BeforeAndAfterEach
import domain.base.Dimensions.TwoDimensionalSpace
import automaton.Cell
import base.Position

class GameOfLifeEnvironmentTest extends AnyFunSuite with BeforeAndAfterEach:
    val dimension = 100
    val env = GameOfLifeEnvironment(dimension)

    test("Initialise environment with dimension <= 0 should throw an error"):
        val exception = intercept[RuntimeException](GameOfLifeEnvironment(0))
        exception shouldBe a[IllegalArgumentException]


    test("Initialize an environment should add cells into the matrix"):
        env.currentMatrix.length should not be 0
    
    test("Initialization should also add alive cells"):
        env.currentMatrix.flatMap(array => array.map(cell => cell.state))
            .filter(state => state == CellState.ALIVE).length should not be 0
    
    test("The neighboors of a cell should always exits"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position((0,0).toList), CellState.DEAD)
        env.neighbours(cell = cell) shouldNot be (List.empty)
