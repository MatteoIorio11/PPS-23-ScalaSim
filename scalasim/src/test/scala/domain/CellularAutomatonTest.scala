package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import utility.DummyAutomaton
import utility.DummyAutomaton.DummyState
import domain.automaton.CellularAutomaton.State
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.Neighbour
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.NeighbourRule

class CellularAutomatonTestq extends AnyFunSuite:
    val ca = DummyAutomaton()

    test("Cellular Automaton's map rule should be empty"):
        ca.rules should not be (Map.empty)
    
    test("Add new rule for the cellular automaton should add it into the automaton's collection"):
        val state: State = DummyState.ALIVE
        val neighborRule: NeighbourRule[TwoDimensionalSpace] = (x: Neighbour[TwoDimensionalSpace]) => Cell(Position(0, 0), DummyState.DEAD)
        ca.addRule(state, neighborRule)
        ca.rules should not be (Map.empty)
    
    test("Apply rule on a specific cell should return the right cell"):
        val cell: Cell[TwoDimensionalSpace] = Cell(Position(0, 0), DummyState.DEAD)
        val state = DummyState.DEAD
        val neighbors = List.empty
        val neighbor: Neighbour[TwoDimensionalSpace] = Neighbour(cell, neighbors)
        val rule: NeighbourRule[TwoDimensionalSpace] = (neighbor) =>
            Cell(Position(0, 0), DummyState.ALIVE)
        ca.addRule(state, rule)
        ca.applyRule(cell, neighbor) shouldBe Cell(Position(0, 0), DummyState.ALIVE)