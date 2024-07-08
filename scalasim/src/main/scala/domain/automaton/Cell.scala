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
  * Factory for [[Cell]] instances.
  */
object Cell:
    def apply[D <: Dimension](p: Position[D], s: State): Cell[D] = CellImpl(p, s)
    def unapply[D <: Dimension](cell: Cell[D]): Option[(Position[D], State)] = Some((cell.position, cell.state))
    private case class CellImpl[D <: Dimension]
      (override val position: Position[D], override val state: State)
    extends Cell[D]