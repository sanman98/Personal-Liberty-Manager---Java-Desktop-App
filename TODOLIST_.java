package personallibertymanager;
import java.awt.event.ActionEvent; // Import for handling action events
import java.awt.event.ActionListener; // Import for action listener interface
import javax.swing.*; // Import for Swing components
import java.awt.*; // Import for AWT components
import java.io.*; // Import for file operations
import java.util.List; // Import for handling lists

public class TODOLIST_ extends JFrame {
    private JPanel panel; // Panel to hold UI components
    private JLabel messageLabel; // Label for task input instructions
    private JTextField taskInput; // Text field for entering tasks
    private JButton addButton; // Button to add a task
    private JButton removeButton; // Button to remove a selected task
    private DefaultListModel<String> listModel; // Model to store tasks for the list
    private JList<String> todoList; // List to display tasks
    private final String SAVE_FILE = "todolist_data.txt"; // File to save tasks
    private final String SIZE_FILE = "window_size.txt"; // File to save window size

    public TODOLIST_() {
        setTitle("To-Do List"); // Set the window title
        loadWindowSize(); // Load the saved window size from file
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure program exits on close
        buildPanel(); // Build the panel and its components
        add(panel); // Add the panel to the frame
        setLocationRelativeTo(null); // Center the window on the screen
        loadTasks(); // Load saved tasks from file
        setVisible(true); // Make the window visible

        // Add a window listener to save tasks and size when the window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveTasks(); // Save tasks to file
                saveWindowSize(); // Save window size to file
            }
        });
    }

    private void buildPanel() {
        panel = new JPanel(); // Create the main panel
        panel.setLayout(new BorderLayout()); // Set layout to BorderLayout
        panel.setBackground(new Color(173, 216, 230)); // Set background color to baby blue

        // Create the list model and JList for displaying tasks
        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(todoList); // Add scroll functionality to the list

        // Create a panel for input components
        JPanel inputPanel = new JPanel(); 
        inputPanel.setLayout(new BorderLayout()); // Set layout to BorderLayout
        inputPanel.setBackground(new Color(173, 216, 230)); // Set background color to baby blue

        messageLabel = new JLabel("Enter a task:"); // Label to prompt task input
        taskInput = new JTextField(20); // Text field for task input with a width of 20 characters
        addButton = new JButton("Add Task"); // Button to add a task
        removeButton = new JButton("Remove Task"); // Button to remove a selected task

        inputPanel.add(messageLabel, BorderLayout.NORTH); // Add label to the top of the input panel
        inputPanel.add(taskInput, BorderLayout.CENTER); // Add text field to the center of the input panel
        inputPanel.add(addButton, BorderLayout.EAST); // Add button to the right of the input panel

        panel.add(inputPanel, BorderLayout.NORTH); // Add input panel to the top of the main panel
        panel.add(listScrollPane, BorderLayout.CENTER); // Add the scrollable list to the center of the main panel
        panel.add(removeButton, BorderLayout.SOUTH); // Add the remove button to the bottom of the main panel

        // Add action listener for the "Add Task" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskInput.getText().trim(); // Get and trim the input text
                if (!task.isEmpty()) { // Check if input is not empty
                    listModel.addElement(task); // Add task to the list model
                    taskInput.setText(""); // Clear the input field
                } else {
                    // Show error message if input is empty
                    JOptionPane.showMessageDialog(panel, "Please enter a task!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener for the "Remove Task" button
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = todoList.getSelectedIndex(); // Get the index of the selected task
                if (selectedIndex != -1) { // Check if a task is selected
                    listModel.remove(selectedIndex); // Remove the selected task
                } else {
                    // Show error message if no task is selected
                    JOptionPane.showMessageDialog(panel, "Please select a task to remove!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Method to save tasks to a file
    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (int i = 0; i < listModel.getSize(); i++) { // Loop through all tasks
                writer.println(listModel.get(i)); // Write each task to the file
            }
        } catch (IOException e) {
            // Show error message if saving fails
            JOptionPane.showMessageDialog(this, "Error saving tasks!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to load tasks from a file
    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String task; // Temporary variable to hold each task
            while ((task = reader.readLine()) != null) { // Read tasks line by line
                listModel.addElement(task); // Add each task to the list model
            }
        } catch (IOException e) {
            // Ignore errors, as the file may not exist initially
        }
    }

    // Method to save window size to a file
    private void saveWindowSize() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SIZE_FILE))) {
            writer.println(getWidth()); // Write the window width
            writer.println(getHeight()); // Write the window height
        } catch (IOException e) {
            // Show error message if saving size fails
            JOptionPane.showMessageDialog(this, "Error saving window size!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to load window size from a file
    private void loadWindowSize() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SIZE_FILE))) {
            int width = Integer.parseInt(reader.readLine()); // Read and parse the width
            int height = Integer.parseInt(reader.readLine()); // Read and parse the height
            setSize(width, height); // Set the window size
        } catch (IOException | NumberFormatException e) {
            setSize(310, 160); // Set default size if file is missing or corrupted
        }
    }

    public static void main(String[] args) {
        new TODOLIST_(); // Create and show the To-Do List application
    }
}