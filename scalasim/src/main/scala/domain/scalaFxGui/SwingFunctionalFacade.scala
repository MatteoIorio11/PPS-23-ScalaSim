package domain.scalaFxGui

import domain.Environment.GenericEnvironment
import domain.base.Dimensions.TwoDimensionalSpace
import domain.engine
import domain.engine.GUIEngine2D

import javax.swing.*
import java.awt.*
import java.util.concurrent.*
import java.util.function.*
import scala.collection.mutable
import domain.engine.Engine.GUIEngine2D
import domain.simulations.gameoflife.GameOfLife.CellState
import scalafx.scene.text.FontWeight.Black

object SwingFunctionalFacade {

    trait Frame {
        def setSize(width: Int, height: Int): Frame
        def addButton(text: String, name: String): Frame
        def addLabel(text: String, name: String): Frame
        def addComboBox(items: Array[String], name: String): Frame
        def addAutomaton(name: String): Frame
        def addPixelPanel(name: String, panel: String): Frame
        def addInput(name: String, panel: String): Frame
        def getAutomatonPanel(name: String): JPanel
        def getSelectedComboBoxItem(name: String): String
        def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], name: String): Frame
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
        private val comboBoxes = mutable.Map[String, JComboBox[String]]()
        private val automatonPanels = mutable.Map[String, PixelPanel]()
        private val pixelPanels = mutable.Map[String, JPanel]()
        private val inputs = mutable.Map[String, JTextField]()
        private var currentAutomaton = ""
        private val eventQueue = new LinkedBlockingQueue[String]()
        private val northPanel = new JPanel()

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
        northPanel.setLayout(new FlowLayout())
        override def setSize(width: Int, height: Int): Frame = {
            jframe.setSize(width, height)
            this
        }

        override def addButton(text: String, name: String): Frame = {
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
        }

        override def addLabel(text: String, name: String): Frame = {
            val jl = new JLabel(text)
            labels.put(name, jl)
            northPanel.add(jl, BorderLayout.NORTH)
            this
        }

        override def addComboBox(items: Array[String], name: String): Frame = {
            val comboBox = new JComboBox[String](items)
            comboBox.setName(name)
            comboBoxes.put(name, comboBox)
            comboBox.addActionListener(_ => {
                try {
                    eventQueue.put(name + "Selected")
                } catch {
                    case _: InterruptedException => // Handle the exception
                }
            })
            northPanel.add(comboBox, BorderLayout.NORTH)
            this
        }

        override def addAutomaton(name: String): Frame = {
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
        }

        override def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], name: String): Frame = {
            val pixelPanel = this.automatonPanels.get(name).get
            guiE = Some(engine.GUIEngine2D(env, pixelPanel))
            jframe.add(pixelPanel, BorderLayout.CENTER)
            jframe.revalidate()
            jframe.repaint()

            guiE.foreach(engine => engine.startEngine)
            this
        }

        override def getSelectedComboBoxItem(name: String): String = {
            val comboBox = comboBoxes(name)
            comboBox.getSelectedItem.asInstanceOf[String]
        }

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

        override def addInput(name: String, panel: String): Frame = {
            if (inputs.contains(name)) {
                throw new IllegalArgumentException(s"An input with name $name already exists.")
            }
            val input = new JTextField()
            inputs.put(name, input)
            input.setVisible(true)
            pixelPanels(panel).add(input)
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
