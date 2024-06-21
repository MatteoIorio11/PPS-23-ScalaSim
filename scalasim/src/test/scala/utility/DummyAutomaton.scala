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


object DummyAutomatonEnvironment:
    def apply(dimension: Int): Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] =
        DummyAutomatonEnvironmentImpl(dimension, DummyAutomaton())

    private case class DummyAutomatonEnvironmentImpl(
        val dimension: Int,
        val cellularAutomata: CellularAutomaton[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]
    )
    extends Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] 
            with ArrayEnvironment2D[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
      require(dimension > 0)
      var matrix: Matrix = Array.ofDim[Array[Cell[TwoDimensionalSpace]]](dimension)
        initialise()
      override protected def initialise(): Unit = 
        print(matrix)
        val initialCell = Cell(Position((-1,-1).toList), DummyState.DEAD)
        val array = Array.fill(dimension, dimension)(initialCell)
        for (y <- 0 until dimension)
            for (x <- 0 until dimension)
                array(x)(y) = Cell(Position((x, y).toList), DummyState.DEAD)
        matrix = array.asInstanceOf[Matrix]


      override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] = ???
      override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] = ???

    @main def main(): Unit = 
        val env = DummyAutomatonEnvironment(10)
        print(env.matrix)


object DummyAutomaton:
    enum DummyState extends State:
        case DEAD
        case ALIVE
    def apply(): CellularAutomaton[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]] = 
        DummyAutomatonImpl()
    private class DummyAutomatonImpl() 
        extends CellularAutomaton[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:
        type Rules = Map[State, Rule[Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]]
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
                .map(rule => rule.applyTransformation(neighbours))
                .getOrElse(Cell(Position((0,0).toList), DummyState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (cellState -> neighborRule)