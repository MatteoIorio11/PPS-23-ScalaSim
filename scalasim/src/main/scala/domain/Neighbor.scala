package domain

import domain.Dimensions.*
import domain.Cell.*

trait Neighbor[D <: Dimension]:
    type Neighborhood = List[Cell[D]]

    def neighborhood: Neighborhood

    def center: Cell[D]
