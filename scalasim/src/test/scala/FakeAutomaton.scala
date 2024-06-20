import domain.automaton.CellularAutomaton.CellularAutomata
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Neighbour
import domain.automaton.Cell
import domain.Environment.Environment
import domain.automaton.CellularAutomaton.*
import domain.base.Position
import domain.base.Position.*
import domain.automaton.Rule
import domain.automaton.NeighbourRule


object FakeAutomaton:
    enum FakeState extends State:
        case DEAD
        case ALIVE
    private case class FakeAutomatonEnvironment(
        val dimension: Int,
        val cellularAutomata: CellularAutomata[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]
    )
     extends Environment[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:

      override protected def availableCells(positions: Iterable[Position[TwoDimensionalSpace]]): Iterable[Cell[TwoDimensionalSpace]] = ???

      override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] = ???

      override def matrix: Matrix = ???

      override protected def initialise(): Unit = ???





    class FakeAutomatonImpl() 
        extends CellularAutomata[TwoDimensionalSpace, Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]:

        type Rules = Map[State, Rule[Neighbour[TwoDimensionalSpace], Cell[TwoDimensionalSpace]]]
        var ruleCollection: Rules = Map()
        override def applyRule(cell: Cell[TwoDimensionalSpace], neighbours: Neighbour[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            ruleCollection.get(cell.state)
                .map(rule => rule.applyTransformation(neighbours))
                .getOrElse(Cell(Position((0,0).toList), FakeState.DEAD))
        override def rules: Rules = ruleCollection
        override def addRule(cellState: State, neighborRule: NeighbourRule[TwoDimensionalSpace]): Unit =
            ruleCollection = ruleCollection + (cellState -> neighborRule)

        

