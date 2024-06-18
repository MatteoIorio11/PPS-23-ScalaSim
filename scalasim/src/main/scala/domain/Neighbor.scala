package domain

import domain.Dimensions.*
import domain.Cell.*

trait Neighbor[D <: Dimension]:
    opaque type Neighborhood = List[Cell[D]]

    def neighborhood(ca: Cell[D]): Neighborhood

    def center(n: Neighborhood): Cell[D]
