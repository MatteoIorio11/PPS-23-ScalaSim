package domain

import domain.Dimensions.*
import domain.Cell.*

trait Neighbor[D <: Dimension]:
    type Neighborhood = List[Cell[D]]

    def neighborhood: Neighborhood

    def center: Cell[D]

object Neighbor:
    def apply[D <: Dimension](center: Cell[D], neighbors: Iterable[Cell[D]]): Neighbor[D] = new NeighborImpl(center, neighbors)
    
    class NeighborImpl[D <: Dimension](override val center: Cell[D], val neighbors: Iterable[Cell[D]]) extends Neighbor[D]:
        override def neighborhood: Neighborhood = neighbors.toList
