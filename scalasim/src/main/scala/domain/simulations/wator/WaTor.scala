package domain.simulations.wator

import domain.utils.ViewBag.ViewBag
import java.awt.Color
import domain.automaton.CellularAutomaton.State
import domain.automaton.CellularAutomaton.ComplexCellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.automaton.Neighbour
import domain.automaton.MultipleOutputNeighbourRule
import domain.automaton.CellularAutomaton.MapSingleRules
import domain.automaton.CellularAutomaton.ValuedState
import domain.automaton.NeighbourRule
import domain.simulations.wator.WaTor.WaTorState
import scala.collection.MapView.Values
import scala.collection.mutable.ArrayBuffer
import domain.Environment.ComplexEnvironment
import domain.Environment.SquareArrayEnvironment2D

object WaTorEnvironment extends ViewBag:
    override def colors: Map[State, Color] = Map((WaTorState.FISH -> Color.GREEN), 
    (WaTorState.SHARK -> Color.BLUE),
    (WaTorState.EMPTY -> Color.BLACK))
    def apply(dimension: Int): ComplexEnvironment[TwoDimensionalSpace] = 
        WaTorEnvironmentImpl(dimension, WaTor())

    private case class WaTorEnvironmentImpl(val side: Int, val cellularAutomata: ComplexCellularAutomaton[TwoDimensionalSpace]) 
        extends ComplexEnvironment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
      require(side > 0)
      var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(WaTorState.EMPTY)
      initialise()
      override protected def initialise() = 
        matrix = matrix.spawnCell(WaTorState.FISH)(WaTorState.SHARK)
    
      override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
          import domain.automaton.NeighborRuleUtility.given
          availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

object WaTor:
    def apply(): ComplexCellularAutomaton[TwoDimensionalSpace] = 
        val wator = WaTor()
        val fishRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] = MultipleOutputNeighbourRule(Some(WaTorState.FISH)): n =>
            val cell = n.neighbourhood.find(cell => cell.state == WaTorState.EMPTY)
            val output = cell match
                case Some(emptyCell) => 
                    emptyCell.state.asInstanceOf[ValuedState[Int]].value match
                        case x if x > 0 => 
                            n.center.state.asInstanceOf[WaTorState].burnEnergy
                            List(Cell(emptyCell.position, n.center.state),
                            Cell(n.center.position, WaTorState.FISH))
                        case _ => List(emptyCell)
                case None => List(n.center)
            output
        val sharkRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] = MultipleOutputNeighbourRule(Some(WaTorState.FISH)): n =>
            n.center.state.asInstanceOf[WaTorState].burnEnergy
            if (n.center.state.asInstanceOf[WaTorState].value == 0)
                List(Cell(n.center.position, WaTorState.EMPTY))
            val possibleCell = n.neighbourhood.find(cell => cell.state == WaTorState.EMPTY || cell.state == WaTorState.FISH)
            val output = possibleCell match
                case Some(fishCell) if fishCell.state == WaTorState.FISH => 
                    n.center.state.asInstanceOf[WaTorState].eatFish
                    List(Cell(n.center.position, WaTorState.EMPTY), Cell(fishCell.position, n.center.state))
                case Some(emptyCell) => 
                    n.center.state.asInstanceOf[WaTorState].value match
                        case energy if energy > 4 => 
                            n.center.state.asInstanceOf[WaTorState].sharkChild
                            List(Cell(n.center.position, n.center.state), Cell(emptyCell.position, n.center.state))
                case None => List(n.center)
            output
        wator.addRule(fishRule)
        wator.addRule(sharkRule)
        wator
    enum WaTorState(var value: Int) extends ValuedState[Int]:
        case FISH extends WaTorState(10)
        case SHARK extends WaTorState(10)
        case EMPTY extends WaTorState(0)
        def burnEnergy: Unit = value = value - 1
        def eatFish: Unit = value = value + 3
        def sharkChild: Unit = value = value / 2

    private case class WaTor() extends ComplexCellularAutomaton[TwoDimensionalSpace]:
        type Rules = Map[State, MultipleOutputNeighbourRule[TwoDimensionalSpace]]
        var ruleCollection: Rules = Map()
        val rules: Rules = ruleCollection
        override def applyRule(neighbors: Neighbour[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] = 
            val cell = neighbors.center
            ruleCollection.get(cell.state) match
                case Some(rule) => rule.applyTransformation(neighbors)
                case None => List()
        override def addRule(neighborRule: MultipleOutputNeighbourRule[TwoDimensionalSpace]) =
            ruleCollection = ruleCollection + (neighborRule.matcher.get -> neighborRule)
