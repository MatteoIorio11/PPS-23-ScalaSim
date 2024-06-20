package domain

import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.CellularAutomaton.CellularAutomata
import domain.simulations.gameoflife.GameOfLife
import automaton.Rule

class RuleTest extends org.scalatest.funsuite.AnyFunSuite:

  test("An identity Rule should return the same input"):
    val r: Rule[Any, Any] = x => x
    val ca = GameOfLife()

    r.applyTransformation(ca) shouldBe ca
    r.applyTransformation((1234, "1234")) shouldBe (1234, "1234")