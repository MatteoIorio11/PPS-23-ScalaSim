package domain

import domain.Dimensions.*
import domain.Neighbor.Position.Position2D
import domain.CellularAutomata.CellularAutomata

object Neighbor:
  trait Position[D <: Dimension]:
    def coordinates: Tuple
    def asPosition[P <: Position[?]]: P = this.asInstanceOf[P]

  object Position:
    def apply(coordinates: Tuple): Position[? <: Dimension] = coordinates match
      case (x: Int, y: Int) => Position2D((x, y))
      case (x: Int, y: Int, z: Int) => Position3D((x, y, z))
      case _ => ???

    class Position2D(private val _coordinates: (Int, Int)) extends Position[TwoDimensionalSpace]:
      override def coordinates: (Int, Int) = _coordinates

    class Position3D(private val _coordinates: (Int, Int, Int)) extends Position[ThreeDimensionalSpace]:
      override def coordinates: (Int, Int, Int) = _coordinates

  /**
    * A NeighborRule represent the set of neighbors position that are needed
    * for computing next automaton step.
    * 
    * @param D the dimensions of the space.
    */
  trait NeighborRule[D <: Dimension] extends Rule[D]:
    opaque type Neighborhood = List[Position[D]]

    /**
      * Returns the neighborhood of the given cellular automaton.
      *
      * @param ca the {@link CellularAutomata}
      * @return the given cellular automaton neighborhood.
      */
    def neighborhood(ca: CellularAutomata[D]): Neighborhood

    type TransitionFunction = (Neighborhood) => (CellularAutomata[D])
  
  /**
    * Generic rule in a N dimensional space that spacefies how cellular 
    * automata of a simulation should transition from a state to another.
    * 
    * @param D the dimension of the space.
    */
  trait Rule[D <: Dimension]:
    
    type TransitionFunction

    /**
      * Applies the transition function specified by this rule to the given automaton
      *
      * @param ca the cellular automaton to apply the transistion function
      * @param tFunc the transition function given by a context.
      * @return the cellular automaton after computing the provided transition function.
      */
    def applyTransformation(ca: CellularAutomata[D])(using tFunc: TransitionFunction): CellularAutomata[D]