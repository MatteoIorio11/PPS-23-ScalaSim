package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import utility.DummyAutomatonEnvironment
import domain.automaton.Cell
import utility.DummyAutomaton.DummyState
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position


class EnvironmentTest extends AnyFunSuite:
    val env = DummyAutomatonEnvironment(10)

    test("Initialise environment with dimension <= 0 should throw an error"):
        val exception = intercept[RuntimeException](DummyAutomatonEnvironment(0))
        exception shouldBe a[IllegalArgumentException]
    
    test("Initialize an environment should add cells into the matrix"):
        env.matrix should not be (Array.empty[Array[Cell[TwoDimensionalSpace]]])
    
    test("Initialization should also add alive cells"):
        env.matrix.asInstanceOf[Array[Array[Cell[TwoDimensionalSpace]]]]
            .flatMap(array => array.map(cell => cell.state))
            .filter(state => state == DummyState.ALIVE).length should not be 0
    
    test("The neighboors of a cell should always exits"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position((0,0).toList), DummyState.DEAD)
        env.neighbours(cell = cell) shouldNot be (List.empty)
  