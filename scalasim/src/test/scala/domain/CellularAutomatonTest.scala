package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import utility.* 
import domain.automaton.CellularAutomaton.State
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.Neighbour
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.NeighbourRule
import _root_.utility.DummyAutomaton
import _root_.utility.DummyAutomaton.DummyState

class CellularAutomatonTestq extends AnyFunSuite:
    val ca = DummyAutomaton()

    test("Cellular Automaton's map rule should be empty"):
        ca.rules should not be (Map.empty)
    
    test("Add new rule for the cellular automaton should add it into the automaton's collection"):
        val neighborRule = NeighbourRule(Some(DummyState.ALIVE))((x: Neighbour[TwoDimensionalSpace]) => Cell(Position(0, 0), DummyState.DEAD))
        ca.addRule(neighborRule)
        ca.rules should not be (Map.empty)
    
    test("Apply rule on a specific cell should return the right cell"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), DummyState.DEAD)
        val neighbors = List.empty
        val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
        val rule = NeighbourRule[TwoDimensionalSpace](Some(DummyState.DEAD))((neighbor) => Cell(Position(0, 0), DummyState.ALIVE))
        ca.addRule(rule)
        ca.applyRule(neighbor) shouldBe Cell(Position(0, 0), DummyState.ALIVE)
