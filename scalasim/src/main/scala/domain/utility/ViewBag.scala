package domain.utility

import java.awt.Color
import domain.automaton.CellularAutomaton.State

object ViewBag:
    trait ViewBag:
        def colors: Map[State, Color]
  
