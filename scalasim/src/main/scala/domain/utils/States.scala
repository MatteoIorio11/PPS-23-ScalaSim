package domain.utils

import domain.automaton.CellularAutomaton.State

trait States:
    def allStates: Set[State]
  
