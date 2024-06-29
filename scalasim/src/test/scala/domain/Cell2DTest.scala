package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.*
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.*
import domain.base.Position

class Cell2DTest extends AnyFunSuite:
    enum MyState extends State:
        case TEST

    test("Creation of a Cell with dimension and state not null should be ok"):
        val state: State = MyState.TEST
        val position = Position(0, 0)
        val cell = Cell(position, state)