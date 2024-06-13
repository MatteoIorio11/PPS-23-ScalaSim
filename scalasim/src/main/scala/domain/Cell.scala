package domain

import domain.Dimensions.*

object CellState:
    /**
      * Cell state that will represent all the Cell's state for Its execution.
      */
    enum State:
        case Alive
        case Dead

trait Cell[D <: Dimension]:
    def position: D
    def state: CellState.State
