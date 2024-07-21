package domain.exporter

import domain.automaton.Cell
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine.Engine.GeneralEngine
import org.jcodec.common.io.{NIOUtils, SeekableByteChannel}
import org.jcodec.common.model.Rational
import org.jcodec.api.awt.AWTSequenceEncoder

import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Trait for converting a matrix of cells to a BufferedImage.
 *
 * @tparam D the type of Dimension
 */
trait MatrixToImageConverter[D <: Dimension] {
  /**
   * Converts a matrix of cells to a BufferedImage.
   *
   * @param matrix        the matrix of cells
   * @param cellSize      the size of each cell in pixels
   * @param stateColorMap the mapping of cell states to colors
   * @return the resulting BufferedImage
   */
  def convert(matrix: LazyList[Cell[D]], cellSize: Int, stateColorMap: Map[State, Color]): BufferedImage
}
  /**
   * Object for converting a matrix of cells in a TwoDimensionalSpace to a BufferedImage.
   */
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

/**
 * Trait for generating a video from a sequence of BufferedImages.
 */
trait VideoGenerator {
  /**
   * Generates a video file from a sequence of BufferedImages.
   *
   * @param videoFilename   the name of the output video file
   * @param images          the sequence of BufferedImages
   * @param secondsPerImage the duration each image is displayed in the video
   */
  def generate(videoFilename: String, images: Seq[BufferedImage], secondsPerImage: Double): Unit
}

/**
 * Object for generating a video using the JCodec library.
 */
object JCodecVideoGenerator extends VideoGenerator {
  /**
   * Generates a video file from a sequence of BufferedImages using the JCodec library.
   *
   * @param videoFilename   the name of the output video file
   * @param images          the sequence of BufferedImages
   * @param secondsPerImage the duration each image is displayed in the video
   */
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

/**
 * Object for exporting a matrix of cells as a video file.
 */
object Exporter {
  /**
   * Exports a matrix of cells as a video file.
   *
   * @param engine          the engine containing the history of matrices
   * @param colors          the mapping of cell states to colors
   * @param converter       the converter for converting matrices to images
   * @param videoGenerator  the generator for creating the video
   * @param cellSize        the size of each cell in pixels
   * @param videoFilename   the name of the output video file
   * @param secondsPerImage the duration each image is displayed in the video
   * @tparam D the type of Dimension
   * @tparam S the type of State
   */
  def exportMatrix[D <: Dimension, S <: State](engine: GeneralEngine[D], colors: Map[State, Color], converter: MatrixToImageConverter[D], videoGenerator: VideoGenerator, cellSize: Int, videoFilename: String, secondsPerImage: Double): Unit = {
    val images = engine.history.zipWithIndex.map { case (matrix, _) =>
      converter.convert(matrix, cellSize, colors)
    }.toList

    videoGenerator.generate(videoFilename, images, secondsPerImage)
  }
}

