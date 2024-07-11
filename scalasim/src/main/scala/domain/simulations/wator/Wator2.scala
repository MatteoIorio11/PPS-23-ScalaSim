package domain.simulations

import domain.automaton.CellularAutomaton.ComplexCellularAutomaton
import domain.automaton.Cell
import domain.base.Dimensions.TwoDimensionalSpace
import scala.util.Random
import domain.automaton.MultipleOutputNeighbourRule
import domain.automaton.CellularAutomaton.ValuedState
import domain.automaton.CellularAutomaton.State
import domain.simulations.WaTorCa.WatorState.*
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

object WaTorEnv extends ViewBag:
    override def colors: Map[State, Color] = Map(
        Fish() -> Color.apply(0, 100, 0),
        Shark() -> Color.RED,
        Water -> Color.CYAN,
    )

    def apply(w: Int, h: Int): ComplexEnvironment[TwoDimensionalSpace] = WatorenvImpl(w, h, WaTorCa())

    private class WatorenvImpl(val width: Int, val heigth: Int, val cellularAutomata: ComplexCellularAutomaton[TwoDimensionalSpace])
        extends ComplexEnvironment[TwoDimensionalSpace] with ArrayToroidEnvironment:

        var matrix: Matrix = ArrayBuffer[ArrayBuffer[Cell[TwoDimensionalSpace]]]().initializeSpace(Water)

        initialise()

        override protected def initialise(): Unit =
            matrix = matrix.spawnCells(100, 1000)(Shark(), Fish())

        override def neighbours(cell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            import domain.automaton.NeighborRuleUtility.given
            availableCells(circleNeighbourhoodLocator.absoluteNeighboursLocations(cell.position))

object WaTorCa:
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

    private class WTCA extends ComplexCellularAutomaton[TwoDimensionalSpace]:
      protected var rules: Map[State, MultipleOutputNeighbourRule[TwoDimensionalSpace]] = Map()

      override def addRule(rule: MultipleOutputNeighbourRule[TwoDimensionalSpace]): Unit = 
        rules = rules + (rule.matcher.getOrElse(AnyState) -> rule)
        

    def apply(): ComplexCellularAutomaton[TwoDimensionalSpace] =
        val ca: ComplexCellularAutomaton[TwoDimensionalSpace] = WTCA()
        ca.addRule(fishRule)
        ca.addRule(sharkRule)
        ca

    private def findRandomCellThat(cells: List[Cell[TwoDimensionalSpace]])(p: Cell[TwoDimensionalSpace] => Boolean): Option[Cell[TwoDimensionalSpace]] =
        val filteredcells = cells.filter(p)
        filteredcells.size match
            case 0 => None
            case x => Some(filteredcells(Random.nextInt(x)))

    private def fishRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] =
        def incrementChronon(fish: Cell[TwoDimensionalSpace], newPosition: Option[Position[TwoDimensionalSpace]] = None): Cell[TwoDimensionalSpace] = newPosition match
            case Some(pos) => Cell[TwoDimensionalSpace](pos, fish.state.asFish.update(_ + 1))
            case None      => Cell[TwoDimensionalSpace](fish.position, fish.state.asFish.update(_ + 1))

        MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(Fish())): n =>
            findRandomCellThat(n.neighbourhood)(_.state == Water) match
                case None => Iterable(incrementChronon(n.center))
                case Some(freeCell) =>
                    n.center.state.asFish.value match
                        case `fishReproductionThreshold` =>
                            Iterable(
                                Cell[TwoDimensionalSpace](freeCell.position, Fish(1)),
                                Cell[TwoDimensionalSpace](n.center.position, Fish()),
                            )
                        case _ => 
                            Iterable(
                                incrementChronon(n.center, Some(freeCell.position)),
                                Cell[TwoDimensionalSpace](n.center.position, Water),
                            )


    private def sharkRule: MultipleOutputNeighbourRule[TwoDimensionalSpace] =
        def updateSharkInfoWhenStepDone(shark: Cell[TwoDimensionalSpace], newPosition: Option[Position[TwoDimensionalSpace]] = None): Cell[TwoDimensionalSpace] = newPosition match
            case Some(pos) => Cell[TwoDimensionalSpace](pos, shark.state.asShark.update(v => v.copy(chrono = v.chrono + 1, energy = v.energy - 1)))
            case None      => Cell[TwoDimensionalSpace](shark.position, shark.state.asShark.update(v => v.copy(chrono = v.chrono + 1, energy = v.energy - 1)))

        def getOutputCells(center: Cell[TwoDimensionalSpace], freeCell: Cell[TwoDimensionalSpace]): Iterable[Cell[TwoDimensionalSpace]] =
            val oldCell = center.state.asShark.value.chrono match
                case `sharkReproductionThreshold` => Cell[TwoDimensionalSpace](center.position, Shark())
                case _                            => Cell[TwoDimensionalSpace](center.position, Water)
            Iterable(updateSharkInfoWhenStepDone(Cell(center.position, center.state.asShark.update(i => i.copy(chrono = 0))), Some(freeCell.position)), oldCell)

        MultipleOutputNeighbourRule[TwoDimensionalSpace](Some(Shark())): n =>
            if n.center.state.asShark.value.energy == 0
            then Iterable(Cell[TwoDimensionalSpace](n.center.position, Water))
            else findRandomCellThat(n.neighbourhood)(_.state == Fish()) match
                    case None => findRandomCellThat(n.neighbourhood)(_.state == Water) match
                        case None => Iterable(updateSharkInfoWhenStepDone(n.center))
                        case Some(freeCell) => getOutputCells(n.center, freeCell)
                    case Some(fishCell) => getOutputCells(Cell(n.center.position, n.center.state.asShark.update(v => v.copy(energy = v.energy + sharkEatFishEnergy))), fishCell)

    extension (s: State)
        private def asShark: Shark = s.asInstanceOf[Shark]
        private def asFish: Fish = s.asInstanceOf[Fish]
