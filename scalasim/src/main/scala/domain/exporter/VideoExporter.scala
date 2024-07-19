package domain.exporter

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine.Engine.GeneralEngine
import domain.simulations.gameoflife.GameOfLife.CellState
import org.jcodec.common.io.{NIOUtils, SeekableByteChannel}
import org.jcodec.common.model.Rational
import org.jcodec.api.awt.AWTSequenceEncoder

import java.awt.Color
import java.awt.image.BufferedImage
import domain.engine.Engine.GeneralEngine
trait MatrixToImageConverter[D <: Dimension] {
  def convert(matrix: LazyList[Cell[D]], cellSize: Int, stateColorMap: Map[State, Color]): BufferedImage
}

  object SimpleMatrixToImageConverter extends MatrixToImageConverter[TwoDimensionalSpace] {
    def convert(matrix: LazyList[Cell[TwoDimensionalSpace]], cellSize: Int, stateColorMap: Map[State, Color]): BufferedImage = {
      val (maxRow, maxCol) = matrix.foldLeft((0, 0)) {
        case ((maxRow, maxCol), cell) => (math.max(maxRow, cell.position.coordinates.head), math.max(maxCol, cell.position.coordinates.last))
      }

      val imgWidth = (maxCol + 1) * cellSize
      val imgHeight = (maxRow + 1) * cellSize

      val bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
      val graphics = bufferedImage.createGraphics()

      matrix.foreach { cell =>
        val color = stateColorMap.getOrElse(cell.state, Color.WHITE)
        graphics.setColor(color)
        graphics.fillRect(cell.position.coordinates.head * cellSize, cell.position.coordinates.last * cellSize, cellSize, cellSize)
      }

      graphics.dispose()
      bufferedImage
    }
  }

trait VideoGenerator {
  def generate(videoFilename: String, images: Seq[BufferedImage], secondsPerImage: Double): Unit
}

object JCodecVideoGenerator extends VideoGenerator {
  def generate(videoFilename: String, images: Seq[BufferedImage], secondsPerImage: Double): Unit =
    var out: SeekableByteChannel = null
    try
      out = NIOUtils.writableFileChannel(videoFilename)
      val encoder = new AWTSequenceEncoder(out, Rational.R(25, 1))
      val framesPerImage = (secondsPerImage * 25).toInt

      images.foreach { image =>
        for (_ <- 1 to framesPerImage)
          encoder.encodeImage(image)
      }

      encoder.finish()
    finally
      NIOUtils.closeQuietly(out)
}

object Exporter {
  def exportMatrix[D <: Dimension, S <: State](engine: GeneralEngine[D], colors: Map[State, Color], converter: MatrixToImageConverter[D], videoGenerator: VideoGenerator, cellSize: Int, videoFilename: String, secondsPerImage: Double): Unit = {
    val images = engine.history.zipWithIndex.map { case (matrix, _) =>
      converter.convert(matrix, cellSize, colors)
    }.toList

    videoGenerator.generate(videoFilename, images, secondsPerImage)
  }
}

