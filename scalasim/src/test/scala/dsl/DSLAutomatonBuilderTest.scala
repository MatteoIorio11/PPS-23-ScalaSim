package dsl.automaton

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.CellularAutomaton.State
import dsl.automaton.Neighbourhood2DBuilder.*
import domain.base.Position

class DSLAutomatonBuilderTest extends AnyFunSuite:
  val alive: State = new State() {}
  val dead: State = new State() {}

  test("The dsl should map a correct neighborhood"):
    val nrb = Neighbourhood2DBuilder.configureNeighborhood:
      state(alive) | x | state(alive) | n |
      x            | c | x            | n |
      state(alive) | x | state(alive)

    nrb.center.isEmpty should not be true
    nrb.center.get shouldBe Position(List(1, 1))
    

    val expectedPositions = List(
      (0, 0), (0, 2),
      (2, 0), (2, 2),
    ).map(x => Position(x.toList))

    val actualPositions = nrb.cells.map(_.position)

    actualPositions should contain theSameElementsAs expectedPositions