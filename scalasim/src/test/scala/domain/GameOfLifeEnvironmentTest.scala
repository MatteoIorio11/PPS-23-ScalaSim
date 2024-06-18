package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.GameOfLifeEnvironment.*
import org.scalatest.BeforeAndAfterEach

class GameOfLifeEnvironmentTest extends AnyFunSuite with BeforeAndAfterEach:
    val dimension = 100
    val env = GameOfLifeEnvironment(dimension)

    override protected def beforeEach(): Unit = 
        env.start()
    
    test("Initialize an environment should add cells into the matrix"):
        env.matrix.length should not be 0