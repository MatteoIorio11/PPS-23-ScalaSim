package domain

import domain.Dimensions.*
import domain.CellularAutomata.*
import domain.Position.{Position2D, Position3D}
object Cell:
    trait Cell[D <: Dimension]:
        def position: Position[D]
        def state: State
    
    class Cell2D(val position: Position2D, val state: State) extends Cell[TwoDimensionalSpace]:
        require(position != null) ; require(state != null)
    class Cell3D(val position: Position3D, val state: State) extends Cell[ThreeDimensionalSpace]:
        require(position != null) ; require(state != null)