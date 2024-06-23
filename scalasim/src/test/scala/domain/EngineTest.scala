package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.engine.Engine2D
import domain.simulations.gameoflife.GameOfLifeEnvironment
import utility.DummyAutomatonEnvironment
import domain.simulations.gameoflife.GameOfLifeEnvironment.initialCell

class EngineTest extends AnyFunSuite:
    val engine = Engine2D(DummyAutomatonEnvironment(10))

    test("Method start should start the simulation and stop should stop it"):
        engine.running shouldBe false
        engine.startEngine
        engine.running shouldBe true
        engine.stopEngine
        engine.running shouldBe false

    test("The initial state of the Simulation should not be empty"):
        val currSimulation = engine.currentMatrix
        currSimulation should not be (Iterator.empty)

    test("Starting the engine should create a new stage for the simulation environment"):
        val initialState = engine.currentMatrix.flatMap(it => it.map(cell => cell))
        engine.startEngine
        Thread.sleep(100)
        val newState = engine.currentMatrix.flatMap(it => it.map(cell => cell))
        engine.stopEngine
        initialState.collect {
            case cell => (cell, newState.filter(c => c.position == cell.position).head)
        }.forall(x => x._1.state != x._2.state) shouldBe (true)
    
    test("After doing a simulation for a couple of time the history should not be empty"):
        engine.history shouldBe (LazyList.empty)
        engine.startEngine
        Thread.sleep(1000)
        engine.stopEngine
        engine.history shouldNot be (LazyList.empty)

