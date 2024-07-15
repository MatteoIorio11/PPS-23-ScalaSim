package domain.scalaFxGui;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

class SwingFunctionalFacade {

    public static interface Frame {
        Frame setSize(int width, int height);
        Frame addButton(String text, String name);
        Frame addLabel(String text, String name);
        Frame addComboBox(String[] items, String name);
        Frame addAutomaton(String name);
        Frame addPixelPanel(String name, String panel);
        String getSelectedComboBoxItem(String name);
        Frame showToLabel(String text, String name);
        Frame show();
        Frame showAutomaton(String name);
        Supplier<String> events();
    }

    public static Frame createFrame(){
        return new FrameImpl();
    }

    private static class FrameImpl implements Frame {
        private final JFrame jframe = new JFrame();
        private final Map<String, JButton> buttons = new HashMap<>();
        private final Map<String, JLabel> labels = new HashMap<>();
        private final Map<String, JComboBox<String>> comboBoxes = new HashMap<>();
        private final Map<String, JPanel> automatonPanels = new HashMap<>();
        private String currentAutomaton = "";
        private final LinkedBlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();
        private final Supplier<String> events = () -> {
            try{
                return eventQueue.take();
            } catch (InterruptedException e){
                return "";
            }
        };
        public FrameImpl() {
            this.jframe.setLayout(new FlowLayout());
        }

        @Override
        public Frame setSize(int width, int height) {
            this.jframe.setSize(width, height);
            return this;
        }

        @Override
        public Frame addButton(String text, String name) {
            JButton jb = new JButton(text);
            jb.setActionCommand(name);
            this.buttons.put(name, jb);
            jb.addActionListener(e -> {
                try {
                    eventQueue.put(name);
                } catch (InterruptedException ex){}
            });
            this.jframe.getContentPane().add(jb);
            return this;
        }

        @Override
        public Frame addLabel(String text, String name) {
            JLabel jl = new JLabel(text);
            this.labels.put(name, jl);
            this.jframe.getContentPane().add(jl);
            return this;
        }

        @Override
        public Frame addComboBox(String[] items, String name) {
            JComboBox<String> comboBox = new JComboBox<>(items);
            comboBox.setName(name);
            this.comboBoxes.put(name, comboBox);
            comboBox.addActionListener(e -> {
                try {
                    eventQueue.put(name + "Selected");
                } catch (InterruptedException ex) {}
            });
            this.jframe.getContentPane().add(comboBox);
            return this;
        }

        @Override
        public String getSelectedComboBoxItem(String name) {
            JComboBox<String> comboBox = this.comboBoxes.get(name);
            return (String) comboBox.getSelectedItem();
        }

        @Override
        public Supplier<String> events() {
            return events;
        }

        @Override
        public Frame showToLabel(String text, String name) {
            this.labels.get(name).setText(text);
            return this;
        }

        @Override
        public Frame show() {
            this.jframe.setVisible(true);
            return this;
        }

        @Override
        public Frame addAutomaton(String name){
            if (this.automatonPanels.containsKey(name)) {
                throw new IllegalArgumentException("An automaton panel with name " + name + " already exists.");
            }

            JPanel jp = new JPanel();
            jp.setVisible(false);
            this.automatonPanels.put(name, jp);
            this.jframe.getContentPane().add(jp);
            return this;
        }

        @Override
        public Frame addPixelPanel(String name, String panel) {
            if (this.pixelPanels.containsKey(name)) {
                throw new IllegalArgumentException("A pixel panel with name " + name + " already exists.");
            }
            //Check the panel exists

            JPanel jp = new JPanel();

            this.panels.put(name, jp);
            jp.setVisible(true);
            return this;
        }


        @Override
        public Frame showAutomaton(String name) {
            if (!this.automatonPanels.containsKey(name)) {
                throw new IllegalArgumentException("An automaton with name " + name + " does not exist.");
            }

            if (!this.currentAutomaton.isEmpty()) {
                this.automatonPanels.get(currentAutomaton).setVisible(false);
            }
            this.currentAutomaton = name;
            this.automatonPanels.get(currentAutomaton).setVisible(true);
            return this;
        }

    }
}