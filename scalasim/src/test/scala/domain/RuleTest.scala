package domain

import org.scalatest.matchers.should.Matchers.*
import domain.base.Dimensions.TwoDimensionalSpace
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.simulations.gameoflife.GameOfLife
import automaton.Rule
import domain.automaton.NeighbourRule
import utility.DummyAutomaton.DummyState.ALIVE
import utility.DummyAutomaton.DummyState.DEAD
import domain.automaton.CellularAutomaton.AnyState
import domain.automaton.Cell
import domain.base.Position
import domain.automaton.Neighbour
import domain.automaton.NeighborRuleUtility.getNeighboursWithState
import domain.automaton.ParametricNeighbourRule
import domain.automaton.ParametricCell
import domain.automaton.CellularAutomaton.ValuedState
import domain.automaton.ParametricNeighbour
import domain.automaton.MultipleOutputNeighbourRule

class RuleTest extends org.scalatest.funsuite.AnyFunSuite:

  test("An identity Rule should return the same input"):
    val r: Rule[Any, Any, Any] = (x: Any) => x
    val ca = GameOfLife()

    r.applyTransformation(ca) shouldBe ca
    r.applyTransformation((1234, "1234")) shouldBe (1234, "1234")

  test("A rule can represent any functions"):
    val r: Rule[Int, String, Any] = x => s"${x} as a string"
    r.applyTransformation(10) shouldBe "10 as a string"

  test("A basic NeighbourRule should apply transformations"):
    val invertStateRule: NeighbourRule[TwoDimensionalSpace] = NeighbourRule(Some(AnyState)): n =>
      n.center.state match
        case ALIVE => Cell(n.center.position, DEAD)
        case DEAD => Cell(n.center.position, ALIVE)

    val aliveCell = Cell[TwoDimensionalSpace](Position(0, 0), ALIVE)
    val deadCell = Cell[TwoDimensionalSpace](Position(0, 0), DEAD)

    invertStateRule.applyTransformation(Neighbour(aliveCell, List.empty)) shouldBe deadCell
    invertStateRule.applyTransformation(Neighbour(deadCell, List.empty)) shouldBe aliveCell

  test("A NeighbourRule applied to a neighbourhood should apply a transformation"):
    val nRule: NeighbourRule[TwoDimensionalSpace] = NeighbourRule(Some(ALIVE)): n =>
      if getNeighboursWithState(DEAD, n).size >= 2  then Cell(n.center.position, DEAD) else n.center

    val center = Cell[TwoDimensionalSpace](Position(1, 1), ALIVE)
    val neighbourhood: Neighbour[TwoDimensionalSpace] = Neighbour(
      center,
      List(
        Cell(Position(0, 0), DEAD),
        Cell(Position(0, 2), ALIVE),
        Cell(Position(2, 1), DEAD),
      )
    )

    nRule.applyTransformation(neighbourhood).state shouldBe DEAD


  test("A `MultipleOutputNeighbourRule` should yield a collection of cells"):
    import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
    val rule: MultipleOutputNeighbourRule[TwoDimensionalSpace] = MultipleOutputNeighbourRule(Some(ALIVE)): n =>
      // if left neighbour is DEAD, move right and set old cell DEAD and new ALIVE
      // move up otherwise.
      n.neighbourhood.find(_.position == n.center.position - Position[TwoDimensionalSpace](0, 1)) match
        case Some(Cell(p, s)) if s == DEAD => List(
          Cell(n.center.position, DEAD),
          Cell(n.center.position + Position[TwoDimensionalSpace](0, 1), ALIVE)
        )
        case _ => List(
          Cell(n.center.position, DEAD),
          Cell(n.center.position - Position[TwoDimensionalSpace](1, 0), ALIVE)
        )
      
    rule.matcher shouldBe Some(ALIVE)

    val oldCenter = Cell[TwoDimensionalSpace](Position(1, 1), ALIVE)
    val neighbours = List(
      Cell[TwoDimensionalSpace](Position(1, 0), DEAD),
      Cell[TwoDimensionalSpace](Position(0, 0), ALIVE),
    )
    val n1: Neighbour[TwoDimensionalSpace] = Neighbour(oldCenter, neighbours)

    val r1: List[Cell[TwoDimensionalSpace]] = rule.applyTransformation(n1).toList

    r1.head shouldBe Cell(Position(1, 1), DEAD)
    r1.last shouldBe Cell(Position(1, 2), ALIVE)

    val n2: Neighbour[TwoDimensionalSpace] = Neighbour( r1.last, neighbours)

    val r2 = rule.applyTransformation(n2)
    r2.head shouldBe Cell(Position(1, 2), DEAD)
    r2.last shouldBe Cell(Position(0, 2), ALIVE)


  test("A `ParametricNeighbourRule` should behave as expected"):
    import domain.automaton.NeighborRuleUtility.PositionArithmeticOperations.*
    val pAlive = new ValuedState[Int] { override def value: Int = 1 }
    val pDead = new ValuedState[Int] { override def value: Int = 0 }

    val rule: ParametricNeighbourRule[TwoDimensionalSpace, Int] =
      ParametricNeighbourRule((x: ParametricCell[TwoDimensionalSpace, Int]) => x.state.value > 0): n =>
        n.neighbourhood.foldLeft(0)((acc, curr) => acc + curr.state.value) match
          case x if x >= 2 => ParametricCell(n.center.position + 1, n.center.state map (_ - 1))
          case _ => ParametricCell(n.center.position, n.center.state map (_ - 1))

    val neighbours = List(
        ParametricCell[TwoDimensionalSpace, Int](Position(0, 1), pAlive),
        ParametricCell[TwoDimensionalSpace, Int](Position(1, 0), pAlive),
        ParametricCell[TwoDimensionalSpace, Int](Position(2, 1), pDead),
      ) 
    
    var center = ParametricCell[TwoDimensionalSpace, Int](Position(1, 1), pAlive)

    val n: ParametricNeighbour[TwoDimensionalSpace, Int] = ParametricNeighbour(
      center,
      neighbours
    )

    center = rule.matcher match
      case Some(pred) if pred(center) => rule.applyTransformation(n)
      case _  => center
    
    val n1 = ParametricNeighbour[TwoDimensionalSpace, Int](
      center,
      neighbours
    )

    val n2Center = rule.matcher match
      case Some(pred) if pred(center) => rule.applyTransformation(n1)
      case _ => center

    n1.center.state.value shouldBe 0
    n1.center.position shouldBe Position(2, 2)

    n2Center shouldBe n1.center