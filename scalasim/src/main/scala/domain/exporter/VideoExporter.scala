package domain.exporter

import domain.base.Dimensions.Dimension
import java.awt.image.BufferedImage

trait MatrixToImageConverter[D <: Dimension, M] {
  def convert(matrix: M, cellSize: Int): BufferedImage
}
