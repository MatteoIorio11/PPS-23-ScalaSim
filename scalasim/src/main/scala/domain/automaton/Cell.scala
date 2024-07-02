package domain.automaton

import domain.base.Dimensions.*
import domain.automaton.CellularAutomaton.*
import domain.base.Position

/**
 * A Cell represents a [[Position]] inside the environment,
 * with an associated [[State]].
 *
 * @param D the [[Dimension]] of the space.
 */
trait Cell[D <: Dimension]:
    def position: Position[D]
    def state: State

/**
  * A specialization of a [[Cell]], with an associated
  * [[ValuedState]] with a value of type [[T]].
  *
  * @param D the [[Dimension]] of the space.
  * @param T the type of the contents of this cell's [[ValuedState]]
  */
trait ParametricCell[D <: Dimension, T] extends Cell[D]:
    override def state: ValuedState[T]

/**
  * Factory for [[Cell]] instances.
  */
object Cell:
    def apply[D <: Dimension](p: Position[D], s: State): Cell[D] = CellImpl(p, s)
    def unapply[D <: Dimension](cell: Cell[D]): Option[(Position[D], State)] = Some((cell.position, cell.state))
    private case class CellImpl[D <: Dimension]
      (override val position: Position[D], override val state: State)
    extends Cell[D]

object ParametricCell:
    def apply[D <: Dimension, T](p: Position[D], s: ValuedState[T]): ParametricCell[D, T] =
      ParametricCellImpl(p, s)
    def unapply[D <: Dimension, T](cell: ParametricCell[D, T]): Option[(Position[D], ValuedState[T])] =
      Some(cell.position, cell.state)
    private case class ParametricCellImpl[D <: Dimension, T]
      (override val position: Position[D], override val state: ValuedState[T])
    extends ParametricCell[D, T]