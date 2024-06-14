package domain

import domain.Dimensions.*

object Cell:
    enum State:
        case Alive
        case Dead

    trait Cell[D <: Dimension]:
        def position: D
        def state: State
