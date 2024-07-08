import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.TwoDimensionalSpace
import domain.engine.Engine2D
import domain.exporter.{Exporter, JCodecVideoGenerator, MatrixToImageConverter, SimpleMatrixToImageConverter, VideoGenerator}
import domain.simulations.briansbrain.BriansBrainEnvironment
import domain.simulations.briansbrain.BriansBrain.CellState
import domain.simulations.gameoflife.GameOfLife.CellState as GameOfLifeState
import domain.simulations.gameoflife.GameOfLifeEnvironment

import java.awt.Color

@main def main(): Unit =
  val engine = Engine2D(GameOfLifeEnvironment(100, 100),5)
  //val engine = Engine2D(BriansBrainEnvironment(100), 5)

  engine.startEngine
  Thread.sleep(2000)
  engine.stopEngine

  Exporter.exportMatrix(
    engine = engine,
    GameOfLifeEnvironment.colors,
    converter = SimpleMatrixToImageConverter,
    videoGenerator = JCodecVideoGenerator,
    cellSize = 10,
    videoFilename = "output.mp4",
    secondsPerImage = 0.1
  )


