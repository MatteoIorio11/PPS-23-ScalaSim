package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.engine.Engine2D
import domain.simulations.gameoflife.GameOfLifeEnvironment

class EngineTest extends AnyFunSuite:
    val engine = Engine2D(GameOfLifeEnvironment(100))

    test("Method start should start the simulation and stop should stop it"):
        engine.running shouldBe false
        engine.start
        engine.running shouldBe true
        engine.stop
        engine.running shouldBe false

  
