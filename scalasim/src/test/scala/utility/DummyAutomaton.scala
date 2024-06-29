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


object DummyAutomatonEnvironment:
    def apply(dimension: Int): Environment[TwoDimensionalSpace] =
        DummyAutomatonEnvironmentImpl(dimension, DummyAutomaton())

    private case class DummyAutomatonEnvironmentImpl(val dimension: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace])
        extends Environment[TwoDimensionalSpace] with ArrayEnvironment2D:
      require(dimension > 0)
      var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]()
      initialise()
      override protected def initialise() = 
        val initialCell = Cell(Position(-1,-1), DummyState.DEAD)
        val array = ArrayBuffer.fill(dimension, dimension)(initialCell)
        for (y <- 0 until dimension)
            for (x <- 0 until dimension)
                val probability = Random().nextBoolean()
                val state = probability match
                    case x if x => DummyState.ALIVE
                    case _ => DummyState.DEAD
                array(x)(y) = Cell(Position(x, y), state)
        array(0)(0) = Cell(Position(0, 0), DummyState.DEAD)
        matrix = array.asInstanceOf[Matrix]

      override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
          import domain.automaton.NeighborRuleUtility.given
          availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)


object DummyAutomaton:
    enum DummyState extends State:
        case DEAD
        case ALIVE
    var automatonColors = Map[DummyState, Color]()
    def apply(): CellularAutomaton[TwoDimensionalSpace] = 
        val dummy = DummyAutomatonImpl()
        automatonColors = automatonColors + (DummyState.DEAD -> Color.RED)
        automatonColors = automatonColors + (DummyState.ALIVE -> Color.GREEN)
        dummy.addRule(DummyState.ALIVE, (x) => Cell(x.center.position, DummyState.DEAD))
        dummy.addRule(DummyState.DEAD, (x) => Cell(x.center.position, DummyState.ALIVE))
        dummy

    private class DummyAutomatonImpl() 
        extends CellularAutomaton[TwoDimensionalSpace]:
        type Rules = Map[State, Rule[Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]]
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]) =
            ruleCollection.get(cell.state)
                .map(rule => rule.applyTransformation(neighbours))
                .getOrElse(Cell(cell.position, DummyState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]) =
            ruleCollection = ruleCollection + (cellState -> neighborRule)
