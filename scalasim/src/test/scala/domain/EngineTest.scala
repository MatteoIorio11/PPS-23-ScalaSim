package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.engine.Engine2D
import domain.simulations.gameoflife.GameOfLifeEnvironment
import utility.DummyAutomatonEnvironment

class EngineTest extends AnyFunSuite:
    val engine = Engine2D(DummyAutomatonEnvironment(10))

    test("Method start should start the simulation and stop should stop it"):
        engine.running shouldBe false
        engine.startEngine
        engine.running shouldBe true
        engine.stopEngine
        engine.running shouldBe false

    test("Run the simulation should return the new state of the simulation"):
        val currSimulation = engine.currentMatrix
        currSimulation should not be (Iterator.empty)
        engine.startEngine
        Thread.sleep(1000)
        val newState = engine.currentMatrix
        engine.stopEngine
        newState should not be currSimulation