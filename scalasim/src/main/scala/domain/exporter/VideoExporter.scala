package domain.exporter

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine.Engine.Engine
import domain.simulations.gameoflife.GameOfLife.CellState
import org.jcodec.common.io.{NIOUtils, SeekableByteChannel}
import org.jcodec.common.model.Rational
import org.jcodec.api.awt.AWTSequenceEncoder

import java.awt.Color
import java.awt.image.BufferedImage
trait MatrixToImageConverter[D <: Dimension, M, S <: State] {
  def convert(matrix: M, cellSize: Int, stateColorMap: Map[S, Color]): BufferedImage
}

object SimpleMatrixToImageConverter extends MatrixToImageConverter[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]], State] {
  def convert(matrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]], cellSize: Int, stateColorMap: Map[State, Color]): BufferedImage = {
    val rows = matrix.size
    val cols = matrix.head.size

    val imgWidth = cols * cellSize
    val imgHeight = rows * cellSize

    val bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.createGraphics()

    matrix.zipWithIndex.foreach { case (row, rowIndex) =>
      row.zipWithIndex.foreach { case (cell, colIndex) =>
        val color = stateColorMap.getOrElse(cell.state, Color.WHITE)
        graphics.setColor(color)
        graphics.fillRect(colIndex * cellSize, rowIndex * cellSize, cellSize, cellSize)
      }
    }

    graphics.dispose()
    bufferedImage
  }
}

trait VideoGenerator {
  def generate(videoFilename: String, images: Seq[BufferedImage], secondsPerImage: Double): Unit
}

object JCodecVideoGenerator extends VideoGenerator {
  def generate(videoFilename: String, images: Seq[BufferedImage], secondsPerImage: Double): Unit = {
    var out: SeekableByteChannel = null
    try {
      out = NIOUtils.writableFileChannel(videoFilename)
      val encoder = new AWTSequenceEncoder(out, Rational.R(25, 1))
      val framesPerImage = (secondsPerImage * 25).toInt

      images.foreach { image =>
        for (_ <- 1 to framesPerImage) {
          encoder.encodeImage(image)
        }
      }

      encoder.finish()
    } finally {
      NIOUtils.closeQuietly(out)
    }
  }
}

object Exporter {
  def exportMatrix[D <: Dimension, M, S <: State](engine: Engine[D, M], converter: MatrixToImageConverter[D, M, S], videoGenerator: VideoGenerator, cellSize: Int, videoFilename: String, secondsPerImage: Double, stateColorMap: Map[S, Color]): Unit = {
    val images = engine.history.zipWithIndex.map { case (matrix, _) =>
      converter.convert(matrix, cellSize, stateColorMap)
    }.toList

    videoGenerator.generate(videoFilename, images, secondsPerImage)
  }
}

