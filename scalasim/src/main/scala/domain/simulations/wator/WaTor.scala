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
import java.util.Random
import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL.neighbour
import domain.Environment.ArrayToroidEnvironment

/**
  * TODO dire come funzio
  */
object WaTorEnvironment extends ViewBag:
    import WaTor.*
    override def colors: Map[State, Color] = Map(
        (FISH() -> Color.apply(0, 100, 0)), 
        (SHARK() -> Color.RED),
        (EMPTY() -> Color.CYAN),
    )

    def apply(w: Int, h: Int): ComplexEnvironment[TwoDimensionalSpace] = 
        WaTorEnvironmentImpl(w, h, WaTor())

    private case class WaTorEnvironmentImpl(val width: Int, val heigth: Int, val cellularAutomata: ComplexCellularAutomaton[TwoDimensionalSpace]) 
        extends ComplexEnvironment[TwoDimensionalSpace] with ArrayToroidEnvironment:

      var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(EMPTY())

      initialise()

      override protected def initialise() = 
        matrix = matrix.spawnCells(1000, 500)(SHARK(), FISH())
    
      override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
          import domain.automaton.NeighborRuleUtility.given
          availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position).toList)

object WaTor:
    protected def findRandom(positions: List[Cell[TwoDimensionalSpace]]): Option[Cell[TwoDimensionalSpace]] = 
        val size = positions.size
        size match
            case x if x > 0 => Some(positions(Random().nextInt(size)))
            case _ => None

    def apply(): ComplexCellularAutomaton[TwoDimensionalSpace] = 
        val wator = WaTor()
        val fishRule = MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(FISH())): neighborus => 
            val emptyCell = findRandom(neighborus.neighbourhood.filter(cell => cell.state.equals(EMPTY())))
            neighborus.center.state match
                case s: FISH => s.burnEnergy
                case _ => 
                emptyCell match
                    case Some(cell) =>
                        neighborus.center.state.asInstanceOf[FISH].canReproduce match
                            case can if can => 
                                Iterable(
                                    Cell(cell.position, FISH()), 
                                    Cell(neighborus.center.position, FISH()))
                            case _ => 
                                Iterable(
                                    Cell(cell.position, neighborus.center.state), 
                                    Cell(neighborus.center.position, EMPTY()))
                    case _ => Iterable(neighborus.center)
        val sharkRule = MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(SHARK())): neighborus => 
            val emptyCell = findRandom(neighborus.neighbourhood
                .filter(cell => cell.state.equals(EMPTY()) ||
                   cell.state.equals(FISH())))
            neighborus.center.state match
                case s: SHARK => s.burnEnergy
                case _ => 
            neighborus.center.state match
                case s: SHARK if s.value == 0 => Iterable(Cell(neighborus.center.position, EMPTY()))
                case _ =>  emptyCell match
                            case Some(cell) => cell.state match
                                case fish: FISH =>  
                                    neighborus.center.state.asInstanceOf[SHARK].eatFish
                                case _ =>
                                val possibleChild = neighborus.center.state.asInstanceOf[SHARK].canReproduce match
                                    case can if can => 
                                        Cell(neighborus.center.position, SHARK(neighborus.center.state.asInstanceOf[SHARK].value))
                                    case _ =>  Cell(neighborus.center.position, EMPTY())
                                Iterable(
                                    possibleChild,
                                    Cell(cell.position, neighborus.center.state))
                            case _ => Iterable(neighborus.center)
        wator.addRule(fishRule)
        wator.addRule(sharkRule)
        wator
    
    abstract case class WaTorState(var value: Int = 10, protected val name: String) extends ValuedState[Int]:
        def burnEnergy: Unit = 
            value = value match
                case x if x > 0 => value - 1
                case _ => value
        override def equals(x: Any): Boolean = 
            x match
                case state: WaTorState => state.name.equals(name)
                case _ => false

        def canReproduce: Boolean

    class FISH extends WaTorState(value = 1000, "FISH"):
        val threshold = value / 2
        def canReproduce: Boolean = value == threshold

    class SHARK(initialEnergy: Int = 100) extends WaTorState(initialEnergy, "SHARK"):
        val threshold = value / 2
        def eatFish: Unit = value = value + 100
        def reproduce: Unit = value = value / 2
        def canReproduce: Boolean = value == threshold

    class EMPTY extends  WaTorState(0, "EMPTY"):
      override def canReproduce: Boolean = false

    private case class WaTor() extends ComplexCellularAutomaton[TwoDimensionalSpace]:
        protected var rules: Map[State, MultipleOutputNeighbourRule[TwoDimensionalSpace]] = Map()
        override def applyRule(neighbors: Neighbour[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] = 
            val cell = neighbors.center
            rules.get(cell.state) match
                case Some(rule) => rule.applyTransformation(neighbors)
                case None => List()
        override def addRule(neighborRule: MultipleOutputNeighbourRule[TwoDimensionalSpace]) =
            rules = rules + (neighborRule.matcher.get -> neighborRule)
