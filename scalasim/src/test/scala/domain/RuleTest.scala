package domain

import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.TwoDimensionalSpace
import domain.CellularAutomata.CellularAutomata
import domain.CellularAutomata.CustomCellularAutomata.CellularAutomata2D

class RuleTest extends org.scalatest.funsuite.AnyFunSuite:

  class IdentityRule extends Rule[TwoDimensionalSpace]:
    type TransitionFunction =  CellularAutomata[TwoDimensionalSpace] => CellularAutomata[TwoDimensionalSpace]

    override def applyTransformation(ca: CellularAutomata[TwoDimensionalSpace])(using tFunc: TransitionFunction): CellularAutomata[TwoDimensionalSpace] = tFunc(ca)

  test("An identity Rule should return the same automaton"):
    val ca = CellularAutomata2D()
    val rule = IdentityRule()

    rule.applyTransformation(ca) shouldBe ca
