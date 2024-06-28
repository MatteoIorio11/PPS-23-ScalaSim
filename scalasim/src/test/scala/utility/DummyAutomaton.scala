package utility

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.Environment.Environment
import domain.automaton.CellularAutomaton.*
import domain.base.Position
import domain.base.Position.*
import domain.automaton.Rule
import domain.automaton.NeighbourRule
import domain.Environment.ArrayEnvironment2D
import utility.DummyAutomaton.DummyState
import domain.automaton.CellularAutomaton
import java.util.Random
import scala.collection.mutable.ArrayBuffer
import org.scalatest.tools.AnsiColor
import org.scalatest.tools.ColorBar
import java.awt.Color
import domain.Environment.SquareArrayEnvironment2D

/**
  * 
  */
object DummyAutomatonEnvironment:
    def apply(dimension: Int): Environment[TwoDimensionalSpace] =
        DummyAutomatonEnvironmentImpl(dimension, DummyAutomaton())

    private case class DummyAutomatonEnvironmentImpl(val side: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace]) 
        extends Environment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
      require(side > 0)
      var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]()
      initialise()
      override protected def initialise() = 
        matrix = matrix.spawnCell(DummyState.ALIVE)
      override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
          import domain.automaton.NeighborRuleUtility.given
          availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

/**
  * 
  */
object DummyAutomaton:
    enum DummyState extends State:
        case DEAD
        case ALIVE
    var automatonColors = Map[DummyState, Color]()
    def apply(): CellularAutomaton[TwoDimensionalSpace] = 
        val dummy = DummyAutomatonImpl()
        automatonColors = automatonColors + (DummyState.DEAD -> Color.RED)
        automatonColors = automatonColors + (DummyState.ALIVE -> Color.GREEN)
        dummy.addRule(DummyState.ALIVE, (x) => Cell(Position((x.center.position.coordinates)), DummyState.DEAD))
        dummy.addRule(DummyState.DEAD, (x) => Cell(Position((x.center.position.coordinates)), DummyState.ALIVE))
        dummy

    private class DummyAutomatonImpl() extends CellularAutomaton[TwoDimensionalSpace] with MapRules2D:
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]) =
            ruleCollection.get(cell.state)
                .map(rule => rule.applyTransformation(neighbours))
                .getOrElse(Cell(cell.position, DummyState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]) =
            ruleCollection = ruleCollection + (cellState -> neighborRule)