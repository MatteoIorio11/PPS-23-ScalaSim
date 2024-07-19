package domain.scalaFxGui

import domain.Environment.GenericEnvironment
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine
import domain.engine.GUIEngine2D

import javax.swing.*
import java.awt.{Color, *}
import java.util.concurrent.*
import java.util.function.*
import scala.collection.mutable
import domain.engine.Engine.GUIEngine2D
import domain.scalaFxGui.EnvironmentOption
import domain.simulations.gameoflife.GameOfLife.CellState

object SwingFunctionalFacade {

    trait Frame {
        def setSize(width: Int, height: Int): Frame
        def addButton(text: String, name: String): Frame
        def addLabel(text: String): Frame
        def addComboBox(items: Array[EnvironmentOption[? <: Dimension, ?]], name: String): Frame
        def addAutomaton(name: String): Frame
        def addPixelPanel(name: String, panel: String): Frame
        def addInput(name: String): Frame
        def getAutomatonPanel(name: String): JPanel
        def getSelectedComboBoxItem(name: String): EnvironmentOption[?, ?]
        def clearSouthPanel(): Frame
        def getInputText(name: String): String
        def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], name: String, colors: Map[State, Color]): Frame
        def showToLabel(text: String, name: String): Frame
        def show(): Frame
        def showAutomaton(name: String): Frame
        def events(): Supplier[String]
    }

    def createFrame(): Frame = new FrameImpl()

    private class FrameImpl extends Frame {
        private val jframe = new JFrame()
        private val buttons = mutable.Map[String, JButton]()
        private val labels = mutable.Map[String, JLabel]()
        private val comboBoxes = mutable.Map[String, JComboBox[?]]()
        private val automatonPanels = mutable.Map[String, PixelPanel]()
        private val pixelPanels = mutable.Map[String, JPanel]()
        private val inputs = mutable.Map[String, JTextField]()
        private val options = mutable.Map[String, EnvironmentOption[? <: Dimension, ?]]()
        private var currentAutomaton = ""
        private val eventQueue = new LinkedBlockingQueue[String]()
        private val northPanel = new JPanel()
        private val southPanel = new JPanel()

        private var guiE: Option[GUIEngine2D] = None

        private val eventsSupplier: Supplier[String] = () => {
            try {
                eventQueue.take()
            } catch {
                case _: InterruptedException => ""
            }
        }

        jframe.setLayout(new BorderLayout())
        jframe.add(northPanel, BorderLayout.NORTH)
        jframe.add(southPanel, BorderLayout.SOUTH)
        northPanel.setLayout(new FlowLayout())
        southPanel.setLayout(new FlowLayout())
        override def setSize(width: Int, height: Int): Frame = {
            jframe.setSize(width, height)
            this
        }

        override def addButton(text: String, name: String): Frame =
            val jb = new JButton(text)
            jb.setActionCommand(name)
            buttons.put(name, jb)
            jb.addActionListener(_ => {
                try {
                    eventQueue.put(name)
                } catch {
                    case _: InterruptedException => // Handle the exception
                }
            })
            northPanel.add(jb, BorderLayout.SOUTH)
            this

        override def addLabel(text: String): Frame =
            val jl = new JLabel(text)
            southPanel.add(jl)
            this

        override def addComboBox(items: Array[EnvironmentOption[? <: Dimension, ?]], name: String): Frame =
            val comboBox = new JComboBox[String](items.map(i => i.name))
            comboBox.setName(name)
            comboBoxes.put(name, comboBox)
            comboBox.addActionListener(_ => {
                try {
                    eventQueue.put(name)
                } catch {
                    case _: InterruptedException => // Handle the exception
                }
            })
            northPanel.add(comboBox, BorderLayout.NORTH)
            this

        override def addAutomaton(name: String): Frame =
            if (automatonPanels.contains(name)) {
                throw new IllegalArgumentException(s"An automaton panel with name $name already exists.")
            }
            val pixelPanel = PixelPanel((1000, 1000), Map(
                CellState.ALIVE -> Color.BLUE,
                CellState.DEAD -> Color.YELLOW
            ))
            pixelPanel.setPixelSize(3)
            automatonPanels.put(name, pixelPanel)
            this


        override def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], name: String, colors: Map[State, Color]): Frame =
            val pixelPanel = PixelPanel((1000, 1000), colors)
            pixelPanel.setPixelSize(3)
            guiE = Some(engine.GUIEngine2D(env, pixelPanel))
            jframe.add(pixelPanel, BorderLayout.CENTER)
            jframe.revalidate()
            jframe.repaint()

            guiE.foreach(engine => engine.startEngine)
            this


        override def getSelectedComboBoxItem(name: String): EnvironmentOption[?, ?] =
            val comboBox = comboBoxes(name)
            val selectedOption = comboBox.getSelectedItem.asInstanceOf[String]
            EnvironmentOption.options.find(o => o.name == selectedOption).getOrElse {
                throw new NoSuchElementException(s"No environment option found for selected item: $selectedOption")
            }


        override def getInputText(name: String): String =
            inputs(name).getText

        override def clearSouthPanel(): Frame =
            inputs.clear()
            southPanel.removeAll()
            southPanel.revalidate()
            southPanel.repaint()
            this



        override def events(): Supplier[String] = eventsSupplier

        override def showToLabel(text: String, name: String): Frame = {
            labels(name).setText(text)
            this
        }

        override def show(): Frame = {
            jframe.setVisible(true)
            this
        }

        override def addPixelPanel(name: String, panel: String): Frame = {
            if (pixelPanels.contains(name)) {
                throw new IllegalArgumentException(s"A pixel panel with name $name already exists.")
            }
            val jp = new JPanel()
            pixelPanels.put(name, jp)
            jp.setVisible(true)
            automatonPanels(panel).add(jp)
            this
        }

        override def addInput(name: String): Frame = {
            if (inputs.contains(name)) {
                throw new IllegalArgumentException(s"An input with name $name already exists.")
            }
            val input = new JTextField(10)
            inputs.put(name, input)
            input.setVisible(true)
            southPanel.add(input)
            southPanel.revalidate()
            southPanel.repaint()
            this
        }

        override def getAutomatonPanel(name: String): JPanel = {
            automatonPanels(name)
        }

        override def showAutomaton(name: String): Frame = {
            if (!automatonPanels.contains(name)) {
                throw new IllegalArgumentException(s"An automaton with name $name does not exist.")
            }
            if (currentAutomaton.nonEmpty) {
                automatonPanels(currentAutomaton).setVisible(false)
            }
            currentAutomaton = name
            automatonPanels(currentAutomaton).setVisible(true)
            this
        }
    }
}
