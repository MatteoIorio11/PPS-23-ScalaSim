package domain

import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Position.Position2D
object Cell:
    trait Cell[D <: Dimension]:
        def position: Position[D]
        def state: State
    
    class Cell2D(val position: Position2D, val state: State) extends Cell[TwoDimensionalSpace]:
        require(position != null)
        require(state != null)
