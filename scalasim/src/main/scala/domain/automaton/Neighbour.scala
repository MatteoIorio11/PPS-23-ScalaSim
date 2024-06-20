package domain.automaton

import domain.base.Dimensions.*
import domain.automaton.Cell.*
import domain.automaton.Cell

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
    type Neighbourhood = List[Cell[D]]

    def neighbourhood: Neighbourhood

    def center: Cell[D]

object Neighbour:
    def apply[D <: Dimension](center: Cell[D], neighbors: Iterable[Cell[D]]): Neighbour[D] = NeighbourImpl(center, neighbors)

    private class NeighbourImpl[D <: Dimension](override val center: Cell[D], val neighbors: Iterable[Cell[D]]) extends Neighbour[D]:
        override def neighbourhood: Neighbourhood = neighbors.toList
