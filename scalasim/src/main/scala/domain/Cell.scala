package domain

import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Position

/**
 * A Cell represents a [[Position]] inside the environment,
 * with an associated [[State]].
 *
 * @param D the [[Dimension]] of the space.
 */
trait Cell[D <: Dimension]:
    def position: Position[D]
    def state: State

object Cell:
    def apply[D <: Dimension](p: Position[D], s: State): Cell[D] = new CellImpl(p, s)
    class CellImpl[D <: Dimension](override val position: Position[D], override val state: State) extends Cell[D]
