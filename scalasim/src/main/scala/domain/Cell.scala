package domain

import domain.Dimensions.*
import domain.CellularAutomata.*
object Cell:
    trait Cell[D <: Dimension]:
        def position: D
        def state: State
    
    class Cell2D(val position: TwoDimensionalSpace, val state: State) extends Cell[TwoDimensionalSpace]:
        require(position != null)
        require(state != null)
