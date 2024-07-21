package domain.gui

import domain.Environment.GenericEnvironment
import domain.automaton.CellularAutomaton.State
import domain.base.Dimensions.{Dimension, TwoDimensionalSpace}
import domain.engine
import domain.engine.GUIEngine2D

import javax.swing.*
import java.awt.*
import java.util.concurrent.*
import java.util.function.*
import scala.collection.mutable
import domain.engine.Engine.GUIEngine2D
import domain.simulations.gameoflife.GameOfLife.CellState

/**
 * SwingFunctionalFacade is used to create and manage a Swing GUI.
 */
object SwingFunctionalFacade {

    /**
     * Frame trait defines the structure and behavior of a GUI frame.
     */
    trait Frame {

        /**
         * Returns the underlying JFrame object.
         *
         * @return the JFrame object
         */
        def frame(): JFrame

        /**
         * Sets the size of the frame.
         *
         * @param width  the width of the frame
         * @param height the height of the frame
         * @return the current Frame instance
         */
        def setSize(width: Int, height: Int): Frame

        /**
         * Adds a button to the frame.
         *
         * @param text the text of the button
         * @param name the name of the button
         * @return the current Frame instance
         */
        def addButton(text: String, name: String): Frame

        /**
         * Adds a label to the frame.
         *
         * @param text the text of the label
         * @return the current Frame instance
         */
        def addLabel(text: String): Frame

        /**
         * Adds a combo box to the frame.
         *
         * @param items the items of the combo box
         * @param name  the name of the combo box
         * @return the current Frame instance
         */
        def addComboBox(items: Array[EnvironmentOption[? <: Dimension, ?]], name: String): Frame

        /**
         * Adds an input field to the frame.
         *
         * @param name the name of the input field
         * @return the current Frame instance
         */
        def addInput(name: String): Frame

        /**
         * Retrieves the selected item from the specified combo box.
         *
         * @param name the name of the combo box
         * @return the selected EnvironmentOption
         */
        def getSelectedComboBoxItem(name: String): EnvironmentOption[?, ?]

        /**
         * Clears the south panel of the frame.
         *
         * @return the current Frame instance
         */
        def clearSouthPanel(): Frame

        /**
         * Clears the main panel of the frame.
         *
         * @return the current Frame instance
         */
        def clearPanel(): Frame

        /**
         * Retrieves the text from the specified input field.
         *
         * @param name the name of the input field
         * @return the text from the input field
         */
        def getInputText(name: String): String

        /**
         * Starts the engine for the specified environment and automaton.
         *
         * @param env    the environment to use
         * @param name   the name of the automaton
         * @param colors the colors for the states of the automaton
         * @return the current Frame instance
         */
        def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], name: String, colors: Map[State, Color]): Frame

        /**
         * Stops the engine.
         *
         * @return the current Frame instance
         */
        def stopEngine(): Frame

        /**
         * Displays text in a label.
         *
         * @param text the text to display
         * @param name the name of the label
         * @return the current Frame instance
         */
        def showToLabel(text: String, name: String): Frame

        /**
         * Makes the frame visible.
         *
         * @return the current Frame instance
         */
        def show(): Frame

        /**
         * Displays the specified automaton in the frame.
         *
         * @param name the name of the automaton
         * @return the current Frame instance
         */
        def showAutomaton(name: String): Frame

        /**
         * Returns a Supplier of event strings.
         *
         * @return a Supplier of event strings
         */
        def events(): Supplier[String]
    }

    /**
     * Creates and returns a new Frame instance.
     *
     * @return a new Frame instance
     */
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
        private val centerPanel = new JPanel()

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
        jframe.add(centerPanel, BorderLayout.CENTER)
        northPanel.setLayout(new FlowLayout())
        southPanel.setLayout(new FlowLayout())
        centerPanel.setLayout(new BorderLayout())

        override def frame(): JFrame =
            jframe
        override def setSize(width: Int, height: Int): Frame =
            jframe.setSize(width, height)
            this

        override def addButton(text: String, name: String): Frame =
            val jb = new JButton(text)
            jb.setActionCommand(name)
            buttons.put(name, jb)
            jb.addActionListener(_ => eventQueue.put(name))
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
            comboBox.addActionListener(_ => eventQueue.put(name))
            northPanel.add(comboBox, BorderLayout.NORTH)
            this

        override def startEngine(env: GenericEnvironment[TwoDimensionalSpace, ?], name: String, colors: Map[State, Color]): Frame =
            val pixelPanel = PixelPanel((1000, 1000), colors)
            pixelPanel.setPixelSize(3)
            guiE = Some(engine.GUIEngine2D(env, pixelPanel))
            centerPanel.add(pixelPanel, BorderLayout.CENTER)
            centerPanel.revalidate()
            centerPanel.repaint()

            guiE.foreach(engine => engine.startEngine)
            this

        override def stopEngine(): Frame =
            guiE.foreach(guiEngine => guiEngine.stopEngine)
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

        override def clearPanel(): Frame =
            inputs.clear()
            centerPanel.removeAll()
            centerPanel.revalidate()
            centerPanel.repaint()
            this

        override def events(): Supplier[String] = eventsSupplier

        override def showToLabel(text: String, name: String): Frame =
            labels(name).setText(text)
            this

        override def show(): Frame =
            jframe.setVisible(true)
            this

        override def addInput(name: String): Frame =
            if (inputs.contains(name))
                throw new IllegalArgumentException(s"An input with name $name already exists.")
            val input = new JTextField(10)
            inputs.put(name, input)
            input.setVisible(true)
            southPanel.add(input)
            southPanel.revalidate()
            southPanel.repaint()
            this

        override def showAutomaton(name: String): Frame =
            if (!automatonPanels.contains(name))
                throw new IllegalArgumentException(s"An automaton with name $name does not exist.")
            if (currentAutomaton.nonEmpty)
                automatonPanels(currentAutomaton).setVisible(false)
            currentAutomaton = name
            automatonPanels(currentAutomaton).setVisible(true)
            this
    }
}
