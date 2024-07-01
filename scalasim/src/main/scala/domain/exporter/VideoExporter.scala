package domain.exporter

import domain.automaton.Cell
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.simulations.gameoflife.GameOfLife.CellState

import java.awt.Color
import java.awt.image.BufferedImage

trait MatrixToImageConverter[D <: Dimension, M] {
  def convert(matrix: M, cellSize: Int): BufferedImage
}

object SimpleMatrixToImageConverter extends MatrixToImageConverter[TwoDimensionalSpace, Iterable[Iterable[Cell[TwoDimensionalSpace]]]] {
  def convert(matrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]], cellSize: Int): BufferedImage = {
    val rows = matrix.size
    val cols = matrix.head.size

    val imgWidth = cols * cellSize
    val imgHeight = rows * cellSize

    val bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.createGraphics()

    matrix.zipWithIndex.foreach { case (row, rowIndex) =>
      row.zipWithIndex.foreach { case (cell, colIndex) =>
        val color = if (cell.state == CellState.ALIVE) Color.BLACK else Color.WHITE
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
