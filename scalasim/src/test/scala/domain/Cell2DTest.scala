package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.*
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.*
import domain.base.Position
import domain.automaton.ParametricCell

class Cell2DTest extends AnyFunSuite:
    enum MyState extends State:
        case TEST

    test("Creation of a Cell with dimension and state not null should be ok"):
        val state: State = MyState.TEST
        val position = Position(0, 0)
        val cell = Cell(position, state)

    test("`unapply` should work as expected"):
        val cells: List[Cell[TwoDimensionalSpace]] = List(Cell(Position(0, 0), MyState.TEST))
        val unappliedCell: Option[(Position[TwoDimensionalSpace], State)] = cells.headOption match
            case Some(Cell(p, s)) => Some((p, s))
            case _ => None

        unappliedCell should not be None
        unappliedCell.get._1 shouldBe Position(0, 0)
        unappliedCell.get._2 shouldBe MyState.TEST

    test("A ParametriCell should behave as expected"):
        val state: ValuedState[Double] = new ValuedState[Double]:
            override def value: Double = 10.0

        val position: Position[TwoDimensionalSpace] = Position(10, 10)
        val cell = ParametricCell(Position(10, 10), state)

        List(cell).headOption match
            case Some(ParametricCell(p, s)) =>
                p shouldBe position
                s shouldBe state
            case _ => fail("Unapply failed")