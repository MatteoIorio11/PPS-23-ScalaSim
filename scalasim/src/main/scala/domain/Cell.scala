package domain

trait Cell[D: Dimension]:
    def position: Dimension
    def state: State
