package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.GameOfLifeEnvironment.*
import domain.GameOfLife.*
import org.scalatest.BeforeAndAfterEach

class GameOfLifeEnvironmentTest extends AnyFunSuite with BeforeAndAfterEach:
    val dimension = 100
    val env = GameOfLifeEnvironment(dimension)

    test("Initialize an environment should add cells into the matrix"):
        env.start()
        env.matrix.length should not be 0