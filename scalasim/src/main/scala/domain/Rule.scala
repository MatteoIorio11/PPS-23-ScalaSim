package domain

import domain.Dimensions.*
import domain.Position
import domain.CellularAutomata.CellularAutomata

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
  def applyTransformation(ca: Cell[D])(using tFunc: TransitionFunction): Cell[D]

object Neighbor:
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
    def neighborhood(ca: Cell[D]): Neighborhood

    type TransitionFunction = (Neighborhood) => (CellularAutomata[D])