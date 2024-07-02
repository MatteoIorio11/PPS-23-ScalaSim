package domain.automaton

import domain.base.Dimensions.*
import domain.automaton.Cell.*
import domain.automaton.Cell
import domain.automaton.ParametricCell

/**
 * A Neighbor represents a collection of [[Cell]]s which are
 * related by a given metric (e.g. euclidean distance in a
 * two/three dimensional world). A neighborhood is composed
 * by a set of cells, with a central [[Cell]], which represents
 * the cell with which the neighborhood is computed.
 *
 * @param D the [[Dimension]] of the space.
 */
trait Neighbour[D <: Dimension]:
    def neighbourhood: List[? <: Cell[D]]
    def center: Cell[D]

/**
  * A [[Neighbour]] of [[ParametricCell]]s.
  *
  * @param D the [[Dimension]] of the space.
  * @param T the type of the parameter held by each [[ParametricCell]].
  */
trait ParametricNeighbour[D <: Dimension, T] extends Neighbour[D]:
    override def neighbourhood: List[ParametricCell[D, T]]
    override def center: ParametricCell[D, T]

object Neighbour:
    def apply[D <: Dimension](center: Cell[D], neighbors: Iterable[Cell[D]]): Neighbour[D] = NeighbourImpl(center, neighbors.toList)

    private case class NeighbourImpl[D <: Dimension](override val center: Cell[D], override val neighbourhood: List[Cell[D]]) extends Neighbour[D]

object ParametricNeighbour:
    def apply[D <: Dimension, T](center: ParametricCell[D, T], neighbours: Iterable[ParametricCell[D, T]]): ParametricNeighbour[D, T] = ParametricNeighbourImpl(center, neighbours.toList)
    private class ParametricNeighbourImpl[D <: Dimension, T]
        (override val center: ParametricCell[D, T], override val neighbourhood: List[ParametricCell[D, T]])
    extends ParametricNeighbour[D, T]