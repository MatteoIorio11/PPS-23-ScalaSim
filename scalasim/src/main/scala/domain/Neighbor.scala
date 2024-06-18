package domain

import domain.Dimensions.*
import domain.Cell.*

trait Neighbor[D <: Dimension]:
    type Neighborhood = List[Cell[D]]

    def neighborhood: Neighborhood

    def center: Cell[D]

object Neighbor:
    abstract class AbstractNeighborhood[D <: Dimension](
        override val center: Cell[D],
        val neighbors: Iterable[Cell[D]],
    ) extends Neighbor[D]:
        override def neighborhood: Neighborhood = neighbors.toList

    case class Neighborhood2D(
        override val center: Cell2D, override val neighbors: Iterable[Cell2D]
    ) extends AbstractNeighborhood[TwoDimensionalSpace](center, neighbors)

    case class Neighborhood3D(
        override val center: Cell3D, override val neighbors: Iterable[Cell3D]
    ) extends AbstractNeighborhood[ThreeDimensionalSpace](center, neighbors)