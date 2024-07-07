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
import scala.annotation.varargs

object WaTorEnvironment extends ViewBag:
    import WaTor.*
    override def colors: Map[State, Color] = Map((FISH() -> Color.GREEN), 
    (SHARK() -> Color.RED),
    (EMPTY() -> Color.CYAN))
    def apply(dimension: Int): ComplexEnvironment[TwoDimensionalSpace] = 
        WaTorEnvironmentImpl(dimension, WaTor())

    private case class WaTorEnvironmentImpl(val side: Int, val cellularAutomata: ComplexCellularAutomaton[TwoDimensionalSpace]) 
        extends ComplexEnvironment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
      require(side > 0)
      var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(EMPTY())
      initialise()
      override protected def initialise() = 
        matrix = matrix.spawnCell(100)(SHARK()).spawnCell(1000)(FISH())
    
      override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
          import domain.automaton.NeighborRuleUtility.given
          availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

object WaTor:
    def apply(): ComplexCellularAutomaton[TwoDimensionalSpace] = 
        val wator = WaTor()
        val fishRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] = MultipleOutputNeighbourRule(Some(FISH())): n =>
            val cell = n.neighbourhood.find(cell => cell.state == EMPTY())
            val output = cell match
                case Some(emptyCell) => 
                    n.center.state.asInstanceOf[FISH].value match
                        case x if x > 4 => 
                            n.center.state.asInstanceOf[WaTorState].burnEnergy
                            List(Cell(emptyCell.position, n.center.state),
                            Cell(n.center.position, FISH()))
                        case _ => List(Cell(emptyCell.position, n.center.state),
                        Cell(n.center.position, EMPTY()))
                case None => List(n.center)
            output
        val sharkRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] = MultipleOutputNeighbourRule(Some(SHARK())): n =>
            n.center.state.asInstanceOf[WaTorState].burnEnergy
            if (n.center.state.asInstanceOf[WaTorState].value == 0)
                List(Cell(n.center.position, EMPTY()))
            else
                val possibleCell = n.neighbourhood.find(cell => cell.state == EMPTY() || cell.state == FISH())
                val output = possibleCell match
                    case Some(fishCell) if fishCell.state == FISH() => 
                        n.center.state.asInstanceOf[SHARK].eatFish
                        var o = List()
                        o.appendedAll(n.center.state.asInstanceOf[WaTorState].value match
                            case energy if energy >= 4 => 
                                n.center.state.asInstanceOf[SHARK].reproduce
                                val energy = n.center.state.asInstanceOf[WaTorState].value
                                List(Cell(n.center.position, SHARK(energy)), Cell(fishCell.position, SHARK(energy)))
                            case _ => 
                                List(Cell(n.center.position, EMPTY()), Cell(fishCell.position, n.center.state)))
                    case Some(emptyCell) => 
                        n.center.state.asInstanceOf[WaTorState].value match
                            case energy if energy >= 4 => 
                                n.center.state.asInstanceOf[SHARK].reproduce
                                val energy = n.center.state.asInstanceOf[WaTorState].value
                                List(Cell(n.center.position, SHARK(energy)), Cell(emptyCell.position, SHARK(energy)))
                            case _ => 
                                List(Cell(n.center.position, EMPTY()), Cell(emptyCell.position, n.center.state))
                    case None => 
                        List(n.center)
                output
        wator.addRule(fishRule)
        wator.addRule(sharkRule)
        wator
    
    case class WaTorState(var value: Int = 10, protected val name: String) extends ValuedState[Int]:
        def burnEnergy: Unit = 
            value = value match
                case x if x > 0 => value - 1
                case _ => value
            
        override def equals(x: Any): Boolean = 
            val otherName = x.asInstanceOf[WaTorState].name
            return this.name == otherName
    class FISH() extends WaTorState(value = 10, "FISH")
    class SHARK(value: Int = 10) extends WaTorState(value, "SHARK"):
        def eatFish: Unit = value = value + 3
        def reproduce: Unit = value = value / 2
        def canReproduce: Boolean = value >= 4
    class EMPTY() extends  WaTorState(0, "EMPTY")
        
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
