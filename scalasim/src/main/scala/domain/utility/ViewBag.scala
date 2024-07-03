package domain.utility

import java.awt.Color
import domain.automaton.CellularAutomaton.State

object ViewBag:
    /**
      * This trait should be used in order to configure all the relevants aspect of the environment during the
      * execution done by the engine, more in particular this Trait specify how to render the different Cellular
      * Automaton's states with colors.
      */
    trait ViewBag:
        /**
          * Colors to use for each state, a specific key define a specific color.
          */
        def colors: Map[State, Color]
  
