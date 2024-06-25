package domain.exporter
import domain.automaton.Cell
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.base.Dimensions.TwoDimensionalSpace
import domain.simulations.gameoflife.GameOfLife
import domain.simulations.gameoflife.GameOfLife.CellState

import java.nio.file.{DirectoryStream, Files, Path, Paths}
import javax.imageio.ImageIO
import org.jcodec.common.io.{NIOUtils, SeekableByteChannel}
import org.jcodec.common.model.Rational
import org.jcodec.api.awt.AWTSequenceEncoder

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Image {
  def exportMatrixToImage(matrix: Iterable[Iterable[Cell[TwoDimensionalSpace]]], cellSize: Int, filePath: String): Unit = {
    val rows = matrix.size
    val cols = matrix.head.size
    val matrixList = matrix.map(_.toList).toList

    val imgWidth = cols * cellSize
    val imgHeight = rows * cellSize

    val bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.createGraphics()

    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val cell = matrixList(row)(col)
        val color = if (cell.state == CellState.ALIVE) Color.BLACK else Color.WHITE // Assumi che `Cell[TwoDimensionalSpace]` possa essere confrontato con 1
        graphics.setColor(color)
        graphics.fillRect(col * cellSize, row * cellSize, cellSize, cellSize)
      }
    }
    graphics.dispose()
    ImageIO.write(bufferedImage, "png", new File(filePath))
  }
}


def sortByNumber(files: Array[File]): Unit = {
  files.sortWith { (o1, o2) =>
    val n1 = extractNumber(o1.getName)
    val n2 = extractNumber(o2.getName)
    n1 - n2 > 0
  }
}
def extractNumber(name: String): Int = {
  try {
    val s = name.lastIndexOf('_') + 1
    val e = name.lastIndexOf('.')
    val number = name.substring(s, e)
    number.toInt
  } catch {
    case _: Exception => 0 // if filename does not match the format then default to 0
  }
}

def generateVideoBySequenceImages(videoFilename: String, pathImages: String, imageExt: String, secondsPerImage: Double): Unit = {
  var out: SeekableByteChannel = null
  try {
    out = NIOUtils.writableFileChannel(videoFilename)

    // for Android use: AndroidSequenceEncoder
    val encoder = new AWTSequenceEncoder(out, Rational.R(25, 1))

    val directoryPath = Paths.get(new File(pathImages).toURI)

    if (Files.isDirectory(directoryPath)) {
      val stream: DirectoryStream[Path] = Files.newDirectoryStream(directoryPath, "*." + imageExt)

      val filesList = new java.util.ArrayList[File]()
      stream.forEach(path => filesList.add(path.toFile))

      val files = filesList.toArray(new Array[File](filesList.size))
      sortByNumber(files)

      for (img <- files) {
        println(s"Encoding image ${img.getName}")
        // Generate the image, for Android use Bitmap
        val image: BufferedImage = ImageIO.read(img)
        // Encode the image multiple times to increase the duration
        for (_ <- 1 to (secondsPerImage * 25).toInt) { // Assuming 25 fps
          encoder.encodeImage(image)
        }
      }
    }
    // Finalize the encoding, i.e. clear the buffers, write the header, etc.
    encoder.finish()
  } finally {
    NIOUtils.closeQuietly(out)
  }
}

