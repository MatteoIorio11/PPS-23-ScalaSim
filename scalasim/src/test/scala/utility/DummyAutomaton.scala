package utility

import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
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
import domain.Environment.ArrayToroidEnvironment
import domain.utils.ViewBag.ViewBag
import domain.Environment.SimpleEnvironment

/**
  * 
  */
object DummyAutomatonEnvironment extends ViewBag:
  override def colors: Map[State, Color] = Map((DummyState.DEAD -> Color.BLACK), (DummyState.ALIVE -> Color.WHITE))

    def apply(dimension: Int): SimpleEnvironment[TwoDimensionalSpace] =
        DummyAutomatonEnvironmentImpl(dimension, DummyAutomaton())

    private case class DummyAutomatonEnvironmentImpl(val side: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace]) 
        extends SimpleEnvironment[TwoDimensionalSpace] with SquareArrayEnvironment2D:
      require(side > 0)
      var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().generalInitialization((side, side))(DummyState.DEAD)
      initialise()
      override protected def initialise() = 
        matrix = matrix.generalSpawn((side, side))(DummyState.DEAD)(DummyState.ALIVE)
      override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
          import domain.automaton.NeighborRuleUtility.MooreNeighbourhood
          Neighbour(
            cell,
            availableCells(MooreNeighbourhood.absoluteNeighboursLocations(cell.position))
          )
object DummyToroidEnv extends ViewBag:
  def apply(w: Int, h: Int): SimpleEnvironment[TwoDimensionalSpace] =
        DummyToroidEnvironmentImpl(w, h, DummyAutomaton())

  private case class DummyToroidEnvironmentImpl(val width: Int, val heigth: Int, val cellularAutomata: CellularAutomaton[TwoDimensionalSpace]) 
      extends SimpleEnvironment[TwoDimensionalSpace] with ArrayToroidEnvironment:
    require(width > 0)
    require(heigth > 0)
    var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().generalInitialization((width, heigth))(DummyState.DEAD)
    initialise()
    override protected def initialise() = matrix = matrix.generalSpawn(dimension)(DummyState.DEAD)(DummyState.ALIVE)
    override def neighbours(cell: Cell[TwoDimensionalSpace]) = 
      import domain.automaton.NeighborRuleUtility.MooreNeighbourhood
      Neighbour(
        cell,
        availableCells(MooreNeighbourhood.absoluteNeighboursLocations(cell.position))
      )
  override def colors: Map[State, Color] = Map((DummyState.DEAD -> Color.BLACK), (DummyState.ALIVE -> Color.WHITE))


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
        dummy.addRule(NeighbourRule(Some(DummyState.ALIVE))((x) => Cell(x.center.position, DummyState.DEAD)))
        dummy.addRule(NeighbourRule(Some(DummyState.DEAD))((x) => Cell(x.center.position, DummyState.ALIVE)))
        dummy

    private class DummyAutomatonImpl() extends CellularAutomaton[TwoDimensionalSpace] with MapSingleRules[TwoDimensionalSpace]:
        var ruleCollection: Rules = Map()
        override def rules: Rules = ruleCollection
        override def addRule(neighborRule: NeighbourRule[TwoDimensionalSpace]) =
            ruleCollection = ruleCollection + (neighborRule.matcher.get -> neighborRule)
