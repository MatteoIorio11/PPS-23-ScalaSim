package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.Cell
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import scala.collection.mutable.ArrayBuffer
import _root_.utility.DummyToroidEnv
import _root_.utility.DummyAutomatonEnvironment.*
import _root_.utility.DummyAutomatonEnvironment
import _root_.utility.DummyAutomaton.DummyState


class EnvironmentTest extends AnyFunSuite:
    val env = DummyAutomatonEnvironment(10)

    test("Initialise environment with dimension <= 0 should throw an error"):
        val exception = intercept[RuntimeException](DummyAutomatonEnvironment(0))
        exception shouldBe a[IllegalArgumentException]
    
    test("Initialize an environment should add cells into the matrix"):
        env.currentMatrix should not be (ArrayBuffer.empty[ArrayBuffer[Cell[TwoDimensionalSpace]]])
    
    test("Initialization should also add alive cells"):
        env.currentMatrix.map(c => c.state)
            .filter(state => state == DummyState.ALIVE).length should not be 0
    
    test("The neighboors of a cell should always exits"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), DummyState.DEAD)
        env.neighbours(cell = cell) shouldNot be (List.empty)

    test("Apply the rule of the CA should return the expected cell"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), DummyState.DEAD)
        val neigh = env.neighbours(cell)
        env.applyRule(neigh) shouldBe Cell(Position(0, 0), DummyState.ALIVE)
class ToroidEnvironmentTest extends AnyFunSuite:
    val env = DummyToroidEnv(10, 4)

    test("Initialize an environment should add cells into the matrix"):
        env.currentMatrix should not be (ArrayBuffer.empty[ArrayBuffer[Cell[TwoDimensionalSpace]]])
    
    test("Initialization should also add alive cells"):
        env.currentMatrix.map(c => c.state)
            .filter(state => state == DummyState.ALIVE).length shouldNot be (0)
    
    test("The neighboors of a cell should always exits and they have to respect the toroid's space"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), DummyState.DEAD)
        val neighbours = List(List(0,9), List(1,9), List(1,0), List(1,1), List(0,1), List(3,9), List(3,1), List(3,0))
        env.neighbours(cell = cell) shouldNot be (List.empty)
        env.neighbours(cell).neighbourhood.map(cell => cell.position.coordinates).forall(it => neighbours.contains(it)) should be (true)

    test("Apply the rule of the CA should return the expected cell"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), DummyState.DEAD)
        val neigh = env.neighbours(cell)
        env.applyRule(neigh) shouldBe Cell(Position(0, 0), DummyState.ALIVE)
