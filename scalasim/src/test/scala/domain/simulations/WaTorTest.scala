package domain.simulations

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.automaton.Neighbour
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.Cell
import domain.base.Position
import domain.simulations.WaTorCellularAutomaton.WatorState.*
import domain.simulations.WaTorCellularAutomaton.*

class WaTorTest extends AnyFunSuite:
  private val waTorCa = WaTorCellularAutomaton()

  test("Custom state equals should work as expected"):
    val f1 = Cell(Position(1, 1), Fish())
    val f2 = Cell(Position(2, 2), Fish())
    val s = Cell(Position(3, 3), Shark())

    f1.state shouldBe f2.state
    f1.state should not be s.state

  test("A fish should move to a free cell"):
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Fish()),
      List(Cell(Position(0, 1), Water))
    )

    waTorCa.applyRule(n) should contain theSameElementsAs List(
      Cell(Position(1, 1), Water),
      Cell(Position(0, 1), Fish())
    )

  test("At each chronon, a fish should increment its counter"):
    var n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Fish()),
      List(Cell(Position(0, 1), Water))
    )

    val newFish = waTorCa.applyRule(n).find(_.position == Position(0, 1)).get

    newFish.state.asInstanceOf[Fish].value shouldBe 1

    n = Neighbour(newFish, List(Cell(Position(1, 1), Water)))

    waTorCa.applyRule(n).find(_.position == Position(1, 1)).get.state.asInstanceOf[Fish].value shouldBe 2
  
  test("A fish should reproduce when reaching threshold"):
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Fish(fishReproductionThreshold)),
      List(Cell(Position(0, 1), Water))
    )

    val newFish = Cell(Position(1, 1), Fish(1))
    val oldFish = Cell(Position(0, 1), Fish())

    val fish = waTorCa.applyRule(n)

    fish should contain theSameElementsAs List(newFish, oldFish)

    fish.find(_.position == newFish.position).get.state.asInstanceOf[Fish].value shouldBe 0
    fish.find(_.position == oldFish.position).get.state.asInstanceOf[Fish].value shouldBe 1

  test("A shark should move to a free cell and should consume 1 unit of energy and increment chronon"):
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Shark()),
      List(Cell(Position(0, 1), Water))
    )


    val res = waTorCa.applyRule(n)

    res should contain theSameElementsAs List(
      Cell(Position(0, 1), Shark()),
      Cell(Position(1, 1), Water),
    )
    
    res.find(_.position == Position(0, 1)).get.state.asInstanceOf[Shark].value shouldBe SharkInfo(1, sharkInitialEnergy - 1)


  test("A shark should reproduce when reaching threshold"):
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Shark(SharkInfo(sharkReproductionThreshold, sharkInitialEnergy))),
      List(Cell(Position(0, 1), Water))
    )

    val newShark = Cell(Position(1, 1), Shark())
    val oldShark = Cell(Position(0, 1), Shark(SharkInfo(1, sharkInitialEnergy - 1)))

    val sharks = waTorCa.applyRule(n)

    sharks should contain theSameElementsAs List(newShark, oldShark)

    sharks.find(_.position == newShark.position).get.state.asInstanceOf[Shark].value shouldBe newShark.state.asInstanceOf[Shark].value
    sharks.find(_.position == oldShark.position).get.state.asInstanceOf[Shark].value shouldBe oldShark.state.asInstanceOf[Shark].value


  test("When energy is zero, a shark should die"):
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Shark(SharkInfo(0, 0))),
      List(Cell(Position(0, 1), Water))
    )

    waTorCa.applyRule(n) should contain theSameElementsAs List(Cell(Position(1, 1), Water))

  test("A shark should prefer a fish over an empty cell, and should gain energy when eating"):
    val n: Neighbour[TwoDimensionalSpace] = Neighbour(
      Cell(Position(1, 1), Shark()),
      List(Cell(Position(0, 1), Fish()), Cell(Position(2, 1), Water))
    )

    val newShark = Cell(Position(0, 1), Shark(SharkInfo(1, sharkInitialEnergy - 1 + sharkEatFishEnergy)))

    val res = waTorCa.applyRule(n)
    res should contain theSameElementsAs List(newShark, Cell(Position(1, 1), Water))
    res.find(_.position == newShark.position).get.state.asInstanceOf[Shark].value shouldBe newShark.state.asInstanceOf[Shark].value

  test("A shark should be alive after some iterations"):
    var shark = Cell[TwoDimensionalSpace](Position(5, 5), Shark(SharkInfo(chrono = 0, energy = 30)))
    var neighbours: Seq[Cell[TwoDimensionalSpace]] = for
      i <- 0 to 5
      j <- 0 until 5
    yield
      if j == i then Cell(Position(i, j), Water) else Cell(Position(i, j), Fish())

    val maxIter = 30
    var currIter = 0
    while currIter < maxIter do
      val n: Neighbour[TwoDimensionalSpace] = Neighbour(shark, neighbours)
      val res = waTorCa.applyRule(n)
      
      res.find(_.state == Shark()) should not be empty
      shark = res.find(_.state == Shark()).get
      if res.size > 1 then
        val otherCell = res.find(_.state != Shark()).getOrElse(Cell(Position(-1, -1), Water))
        neighbours = neighbours.map(c => if c.position == shark.position then otherCell else c)

      currIter += 1
