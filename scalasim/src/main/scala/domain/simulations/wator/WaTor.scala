package domain.simulations

import domain.automaton.CellularAutomaton.ComplexCellularAutomaton
import domain.automaton.Cell
import domain.base.Dimensions.TwoDimensionalSpace
import scala.util.Random
import domain.automaton.MultipleOutputNeighbourRule
import domain.automaton.CellularAutomaton.ValuedState
import domain.automaton.CellularAutomaton.State
import domain.simulations.WaTorCellularAutomaton.WatorState.*
import domain.simulations.WaTorCellularAutomaton.WatorState
import domain.base.Position
import domain.automaton.Neighbour
import domain.automaton.CellularAutomaton.MultiOutputCellularAutomaton
import domain.utils.ViewBag.ViewBag
import java.awt.Color
import domain.Environment.ComplexEnvironment
import domain.Environment.ArrayToroidEnvironment
import scala.collection.mutable.ArrayBuffer
import dsl.automaton.rule.ExplicitNeighbourRuleBuilder.CustomNeighbourhoodDSL.neighbour
import domain.automaton.CellularAutomaton.AnyState

object WaTorEnvironment extends ViewBag:
    override def colors: Map[State, Color] = Map(
        Fish() -> Color.apply(0, 100, 0),
        Shark() -> Color.RED,
        Water -> Color.CYAN,
    )

    def apply(w: Int, h: Int, initialCells: Map[State , Int]): ComplexEnvironment[TwoDimensionalSpace] = WaTorEnvironmentImpl(w, h, initialCells, WaTorCellularAutomaton())

    private class WaTorEnvironmentImpl(val width: Int, val heigth: Int, val initialCells: Map[State , Int], val cellularAutomata: ComplexCellularAutomaton[TwoDimensionalSpace])
        extends ComplexEnvironment[TwoDimensionalSpace] with ArrayToroidEnvironment:

        require(initialCells.values.sum < width * heigth)

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(Water)

        initialise()

        override protected def initialise(): Unit =
            initialCells.foreach((state, amount) => matrix = matrix.spawnCells(amount)(state))
            matrix = matrix.spawnCells(1, 1000)(Shark(), Fish())

        override def neighbours(cell: Cell[TwoDimensionalSpace]): Neighbour[TwoDimensionalSpace] =
            import domain.automaton.NeighborRuleUtility.given
            Neighbour[TwoDimensionalSpace](
                cell,
                availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position))
            )

object WaTorCellularAutomaton:
    val fishReproductionThreshold: Int = 10
    val sharkReproductionThreshold: Int = 10
    val sharkInitialEnergy: Int = 1000
    val sharkEatFishEnergy: Int = 100

    object WatorState:
        sealed trait StateComparison:
            override def equals(x: Any) = x.getClass() == this.getClass()

        case class SharkInfo(val chrono: Int, val energy: Int)

        case class Shark(override val value: SharkInfo = SharkInfo(0, sharkInitialEnergy)) extends ValuedState[SharkInfo] with StateComparison:
            override def update(f: SharkInfo => SharkInfo): Shark = Shark(f(value))

        case class Fish(override val value: Int = 0) extends ValuedState[Int] with StateComparison:
            override def update(f: Int => Int): Fish = Fish(f(value))

        object Water extends State

    private class WaTorCellularAutomatonImpl extends ComplexCellularAutomaton[TwoDimensionalSpace]:
      protected var rules: Map[State, MultipleOutputNeighbourRule[TwoDimensionalSpace]] = Map()

      override def addRule(rule: MultipleOutputNeighbourRule[TwoDimensionalSpace]): Unit = 
        rules = rules + (rule.matcher.getOrElse(AnyState) -> rule)
        

    def apply(): ComplexCellularAutomaton[TwoDimensionalSpace] =
        val ca: ComplexCellularAutomaton[TwoDimensionalSpace] = WaTorCellularAutomatonImpl()
        ca.addRule(fishRule)
        ca.addRule(sharkRule)
        ca

    private def findRandomCellThat(cells: List[Cell[TwoDimensionalSpace]])(p: Cell[TwoDimensionalSpace] => Boolean): Option[Cell[TwoDimensionalSpace]] =
        val filteredcells = cells.filter(p)
        filteredcells.size match
            case 0 => None
            case x => Some(filteredcells(Random.nextInt(x)))

    private def fishRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] =
        def incrementChronon(fish: Cell[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            Cell(fish.position, fish.state.asFish.update(_ + 1))

        def moveFishTo(from: Cell[TwoDimensionalSpace], to: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            computeOldCell(from) match
                case Cell(p, Water) => Iterable(Cell(to.position, incrementChronon(from).state), Cell(p, Water))
                case Cell(p, s)     => Iterable(Cell(to.position, Fish(1)), Cell(p, s))

        def computeOldCell(fish: Cell[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] =
            fish.state.asFish.value match
                case `fishReproductionThreshold` => Cell(fish.position, Fish())
                case _                           => Cell(fish.position, Water)
            
        MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(Fish())): n =>
            findRandomCellThat(n.neighbourhood)(_.state == Water) match
                case None => Iterable(incrementChronon(n.center))
                case Some(freeCell) => moveFishTo(n.center, freeCell)

    private def sharkRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] =
        def incrementSharkStats(shark: Cell[TwoDimensionalSpace], energyIncrement: Option[Int] = None): Cell[TwoDimensionalSpace] =
            val eInc = if energyIncrement.isEmpty then 0 else energyIncrement.get
            Cell(shark.position, shark.state.asShark.update(i => SharkInfo(i.chrono + 1, i.energy - 1 + eInc)))

        def moveSharkTo(from: Cell[TwoDimensionalSpace], to: Cell[TwoDimensionalSpace], eatingFish: Boolean = false): Iterable[Cell[TwoDimensionalSpace]] =
            val oldCell = computeOldCell(from)
            var newCell = Cell(to.position, incrementSharkStats(from, if eatingFish then Some(sharkEatFishEnergy) else None).state)
            if oldCell.state == Shark() then newCell = Cell(newCell.position, newCell.state.asShark.update(_.copy(chrono = 1)))
            Iterable(newCell, oldCell)

        def computeOldCell(shark: Cell[TwoDimensionalSpace]): Cell[TwoDimensionalSpace] = shark.state.asShark.value.chrono match
            case `sharkReproductionThreshold` => Cell(shark.position, Shark())
            case _                            => Cell(shark.position, Water)

        MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(Shark())): n =>
            if n.center.state.asShark.value.energy == 0
            then Iterable(Cell[TwoDimensionalSpace](n.center.position, Water))
            else findRandomCellThat(n.neighbourhood)(_.state == Fish()) match
                    case None => findRandomCellThat(n.neighbourhood)(_.state == Water) match
                        case None => Iterable(incrementSharkStats(n.center))
                        case Some(freeCell) => moveSharkTo(n.center, freeCell)
                    case Some(fishCell) => moveSharkTo(n.center, fishCell, true)

    extension (s: State)
        private def asShark: Shark = s.asInstanceOf[Shark]
        private def asFish: Fish = s.asInstanceOf[Fish]
