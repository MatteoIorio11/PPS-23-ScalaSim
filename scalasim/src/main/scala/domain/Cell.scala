package domain

object CellState:
    enum State:
        case Alive
        case Dead

trait Cell[D <: Dimension]:
    def position: D
    def state: CellState.State
