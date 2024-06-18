package domain

import domain.Dimensions.*
import domain.Cell.*

trait Neighbor[D <: Dimension]:
    type Neighborhood = List[Cell[D]]

    def neighborhood: Neighborhood

    def center: Cell[D]

object Neighbor:
    case class Neighborhood2D(
        override val center: Cell[TwoDimensionalSpace],
        val neighbors: Iterable[Cell[TwoDimensionalSpace]]
    ) extends Neighbor[TwoDimensionalSpace]:
        override def neighborhood: Neighborhood = neighbors.toList