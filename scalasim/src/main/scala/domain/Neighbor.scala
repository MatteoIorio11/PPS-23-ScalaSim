package domain

import domain.base.Dimensions.*
import domain.automaton.Cell.*
import automaton.Cell

/**
 * A Neighbor represents a collection of [[Cell]]s which are
 * related by a given metric (e.g. euclidean distance in a
 * two/three dimensional world). A neighborhood is composed
 * by a set of cells, with a central [[Cell]], which represents
 * the cell with which the neighborhood is computed.
 *
 * @param D the [[Dimension]] of the space.
 */
trait Neighbor[D <: Dimension]:
    type Neighborhood = List[Cell[D]]

    def neighborhood: Neighborhood

    def center: Cell[D]

object Neighbor:
    def apply[D <: Dimension](center: Cell[D], neighbors: Iterable[Cell[D]]): Neighbor[D] =
        new NeighborImpl(center, neighbors)
    
    class NeighborImpl[D <: Dimension](override val center: Cell[D], val neighbors: Iterable[Cell[D]]) extends Neighbor[D]:
        override def neighborhood: Neighborhood = neighbors.toList
