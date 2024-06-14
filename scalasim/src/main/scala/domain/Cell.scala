package domain

import domain.Dimensions.*
import domain.CellularAutomata.*
object Cell:

    trait Cell[D <: Dimension]:
        def position: D
        def state: State
