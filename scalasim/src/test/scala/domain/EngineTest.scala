package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.engine.Engine2D
import utility.DummyAutomatonEnvironment
import org.scalatest.BeforeAndAfterEach
import scala.collection.mutable.ArrayBuffer
import domain.engine.FastEngine2D
import domain.automaton.CellularAutomaton.CellularAutomaton
import domain.automaton.Cell

class EngineTest extends AnyFunSuite with BeforeAndAfterEach:
    var engine = Engine2D(DummyAutomatonEnvironment(10), 100)
    
    override protected def beforeEach(): Unit = 
        engine = Engine2D(DummyAutomatonEnvironment(10), 100)


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
        engine.history should be (LazyList.empty)
        val initMatrix = engine.currentMatrix
        engine.startEngine
        Thread.sleep(1000)
        engine.stopEngine
        val lastMatrix = engine.currentMatrix
        engine.history shouldNot be (LazyList.empty)
        engine.history.head shouldBe (initMatrix)
        engine.history.last shouldBe (lastMatrix)
        engine.history(0) shouldNot be (engine.history(1))

class FastEngine extends AnyFunSuite with BeforeAndAfterEach:
    val timer = 2
    var engine = FastEngine2D(DummyAutomatonEnvironment(10), timer)

    override protected def beforeEach(): Unit = 
        engine = FastEngine2D(DummyAutomatonEnvironment(10), timer)
    
    test("Starting the engine should create a new stage for the simulation environment"):
        val initialState = engine.currentMatrix.flatMap(it => it.map(cell => cell))
        engine.startEngine
        Thread.sleep((timer + 1) * 1000)
        val newState = engine.currentMatrix.flatMap(it => it.map(cell => cell))
        engine.stopEngine
        initialState.collect {
            case cell => (cell, newState.filter(c => c.position == cell.position).head)
        }.forall(x => x._1.state != x._2.state) shouldBe (true)
    
    test("After doing a simulation for a couple of time the history should not be empty"):
        engine.history should be (LazyList.empty)
        val initMatrix = engine.currentMatrix
        engine.startEngine
        Thread.sleep((timer + 1) * 1000)
        engine.stopEngine
        val lastMatrix = engine.currentMatrix
        engine.history shouldNot be (LazyList.empty)
        engine.history.head shouldBe (initMatrix)
        engine.history.last shouldBe (lastMatrix)
        engine.history(0) shouldNot be (engine.history(1))