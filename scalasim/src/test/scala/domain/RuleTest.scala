package domain

import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.TwoDimensionalSpace
import domain.CellularAutomata.CellularAutomata
import domain.CellularAutomata.CustomCellularAutomata.CellularAutomata2D

class RuleTest extends org.scalatest.funsuite.AnyFunSuite:
  val ca = CellularAutomata2D()

  test("An identity Rule should return the same automaton"):
    val identityRule = new Rule[TwoDimensionalSpace] {
      type TransitionFunction =  CellularAutomata[TwoDimensionalSpace] => CellularAutomata[TwoDimensionalSpace]

      override def applyTransformation(ca: CellularAutomata[TwoDimensionalSpace])(using tFunc: TransitionFunction): CellularAutomata[TwoDimensionalSpace] = tFunc(ca)
    }

    identityRule.applyTransformation(ca) shouldBe ca