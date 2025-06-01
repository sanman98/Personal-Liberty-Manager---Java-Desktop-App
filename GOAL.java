package personallibertymanager;
import java.awt.event.*; 
import javax.swing.*; 
import java.awt.*; 
import java.io.*; 
import java.util.*; 

public class GOAL extends JFrame {
    private JPanel panel; // Panel to hold components
    private JLabel messageLabel; // Label for instructions
    private JTextField taskInput; // Text field for user input
    private JButton addButton; // Button to add goals
    private JButton removeButton; // Button to remove goals
    private DefaultListModel<String> listModel; // List model to hold goals
    private JList<String> todoList; // JList to display goals
    private static final String GOALS_FILE = "goals.txt"; // File to save goals
    private static final String CONFIG_FILE = "config.txt"; // File to save window dimensions

    public GOAL() {
        setTitle("Goals of the year"); // Set the title of the window
        loadWindowSize(); // Load saved window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close program on exit
        buildPanel(); // Build and set up the GUI components
        add(panel); // Add the panel to the frame
        loadGoals(); // Load saved goals from file
        setLocationRelativeTo(null); // Center the window on the screen
        setVisible(true); // Make the window visible

        // Add a window listener to handle events when the window is closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveGoals(); // Save goals to file
                saveWindowSize(); // Save window size to file
            }
        });
    }

    private void buildPanel() {
        panel = new JPanel(); // Initialize the main panel
        panel.setLayout(new BorderLayout()); // Set layout to BorderLayout
        panel.setBackground(new Color(173, 216, 230)); // Set panel background color to baby blue

        listModel = new DefaultListModel<>(); // Initialize the list model
        todoList = new JList<>(listModel); // Initialize the JList with the model
        JScrollPane listScrollPane = new JScrollPane(todoList); // Add scrolling to the list

        JPanel inputPanel = new JPanel(); // Create a panel for input
        inputPanel.setLayout(new BorderLayout()); // Use BorderLayout for input panel
        inputPanel.setBackground(new Color(173, 216, 230)); // Match background color

        messageLabel = new JLabel("Enter your goal:"); // Instruction label
        taskInput = new JTextField(20); // Text field with 20 columns
        addButton = new JButton("Add goal"); // Button to add goals
        removeButton = new JButton("Remove goal"); // Button to remove selected goals

        inputPanel.add(messageLabel, BorderLayout.NORTH); // Add label to the top
        inputPanel.add(taskInput, BorderLayout.CENTER); // Add text field to the center
        inputPanel.add(addButton, BorderLayout.EAST); // Add button to the right

        panel.add(inputPanel, BorderLayout.NORTH); // Add input panel to the top
        panel.add(listScrollPane, BorderLayout.CENTER); // Add list with scroll pane to the center
        panel.add(removeButton, BorderLayout.SOUTH); // Add remove button to the bottom

        // Add functionality to the Add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String task = taskInput.getText().trim(); // Get user input and trim whitespace
                    if (!task.isEmpty()) {
                        listModel.addElement(task); // Add goal to the list model
                        taskInput.setText(""); // Clear the input field
                    } else {
                        throw new IllegalArgumentException("Goal cannot be empty"); // Handle empty input
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add functionality to the Remove button
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedIndex = todoList.getSelectedIndex(); // Get the selected index
                    if (selectedIndex != -1) {
                        listModel.remove(selectedIndex); // Remove the selected goal
                    } else {
                        throw new IllegalStateException("No goal selected for removal"); // Handle no selection
                    }
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadGoals() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GOALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                listModel.addElement(line); // Add each saved goal to the list model
            }
        } catch (FileNotFoundException e) {
            saveGoals();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading goals file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveGoals() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GOALS_FILE))) {
            for (int i = 0; i < listModel.getSize(); i++) {
                writer.println(listModel.getElementAt(i)); // Write each goal to the file
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving goals.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadWindowSize() {
        try (Scanner scanner = new Scanner(new File(CONFIG_FILE))) {
            if (scanner.hasNextInt()) {
                int width = scanner.nextInt(); // Read width from the file
                int height = scanner.nextInt(); // Read height from the file
                setSize(width, height); // Set the window size
            } else {
                setSize(310, 160); // Set default size if file is empty
            }
        } catch (FileNotFoundException e) {
            setSize(310, 160); // Default size if file doesn't exist
            saveWindowSize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading window size.", "Error", JOptionPane.ERROR_MESSAGE);
            setSize(310, 160); // Fallback to default size in case of error
        }
    }

    private void saveWindowSize() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            writer.println(getWidth() + " " + getHeight()); // Save current window dimensions
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving window size.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new GOAL(); // Start the application
    }
}