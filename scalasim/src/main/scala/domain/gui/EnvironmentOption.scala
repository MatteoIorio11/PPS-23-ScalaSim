package domain.gui

import domain.Environment.GenericEnvironment
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.simulations.WaTorCellularAutomaton.WatorState.{Fish, Shark}
import domain.simulations.WaTorEnvironment
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.simulations.briansbrain.BriansBrainEnvironment
import domain.simulations.gameoflife.GameOfLifeEnvironment
import domain.simulations.langtonsant.LangtonsAntEnvironment
import domain.simulations.gameoflife.GameOfLife.CellState as GameOfLifeState

import java.awt.Color

case class EnvironmentOption[D <: Dimension, R](name: String, createEnvironment: (Int, Int, Map[? <:State, Int]) => GenericEnvironment[TwoDimensionalSpace, R], colors: Map[State, Color], isToroidal: Boolean, states: List[State])

object EnvironmentOption:
  val options = List(
    EnvironmentOption("Brian's Brain", (width, height, initialCells) => BriansBrainEnvironment(width, initialCells), BriansBrainEnvironment.colors, false, List(CellState.ON, CellState.OFF, CellState.DYING)),
    EnvironmentOption("Game of Life", (width, height, initialCells) => GameOfLifeEnvironment(width, height, initialCells), GameOfLifeEnvironment.colors, true, List(GameOfLifeState.ALIVE, GameOfLifeState.DEAD)),
    EnvironmentOption("Wa Tor", (width, height, initialCells) => WaTorEnvironment(width, height, initialCells), WaTorEnvironment.colors, true, List(Fish(), Shark())),
    EnvironmentOption("Langton's Ant", (width, height, initialCells) => LangtonsAntEnvironment(width), LangtonsAntEnvironment.colors, true, List())
  )
