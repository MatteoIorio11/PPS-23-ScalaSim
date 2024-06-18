package domain

import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.TwoDimensionalSpace
import domain.CellularAutomata.CellularAutomata
import domain.GameOfLife

class RuleTest extends org.scalatest.funsuite.AnyFunSuite:

  test("An identity Rule should return the same automaton"):
    val r: Rule[Any, Any] = x => x
    val ca = GameOfLife()

    r.applyTransformation(ca) shouldBe ca
    r.applyTransformation((1234, "1234")) shouldBe (1234, "1234")