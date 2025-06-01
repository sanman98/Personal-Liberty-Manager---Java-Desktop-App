package personallibertymanager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class PersonallibertyManager extends JFrame {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton button1, button2, button3, button4;
    private DefaultListModel<String> notesModel = new DefaultListModel<>();  // List model for notes

    final int WINDOW_WIDTH = 400;
    final int WINDOW_HEIGHT = 300;
    private static final String NOTES_FILE = "notes.txt";  // File path to save and load notes

    public PersonallibertyManager() {
        setTitle("Personal Liberty Manager");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildMainPanel();
        buildMenuBar();
        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Load notes from file
        loadNotesFromFile();
    }

    private void buildMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1));
        buttonPanel.setBackground(new Color(173, 216, 230));

        titleLabel = new JLabel("Personal Liberty Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        buttonPanel.add(titleLabel);

        button1 = new JButton("To-Do List");
        button2 = new JButton("Goals of the Year");
        button3 = new JButton("Books");
        button4 = new JButton("Categories");

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TODOLIST_ toDoList = new TODOLIST_();
                toDoList.setVisible(true);
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              GOAL goals = new GOAL();
                goals.setVisible(true);
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              BooksManager booksManager = new BooksManager();
              booksManager.showBooksManager(); // This will open BooksManager window
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              CategoryManagerWindow category = new CategoryManagerWindow();
              category.showCategoryManager(); // Use the method defined in CategoryManagerWindow to show the window   
            }
        });

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu noteMenu = new JMenu("Note");
        JMenuItem newNoteItem = new JMenuItem("New Note");
        JMenuItem viewNotesItem = new JMenuItem("View/Edit Notes");

        noteMenu.add(newNoteItem);
        noteMenu.add(viewNotesItem);

        newNoteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNoteEditor(null, null);
            }
        });

        viewNotesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNotesManager();
            }
        });

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutUsItem = new JMenuItem("About Us");
        JMenuItem groupMembersItem = new JMenuItem("Group Members");

        helpMenu.add(aboutUsItem);
        helpMenu.add(groupMembersItem);

        aboutUsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    null,
                    "We designed this program to help you manage your tasks and goals." +"\n"+
                    "and it makes it easier for you to remember your books through their names and authors." +"\n"+
                    "You can also classify them under the appropriate category of your choice.",
                    "About Us",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        groupMembersItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Sadeem Awak 2310922\n" +
                    "Rimas Almuntashiri 2311631\n" +
                    "Mariam Bourim 2317479",
                    "Group Members",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        menuBar.add(noteMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showNoteEditor(String existingTitle, String existingContent) {
        JFrame noteFrame = new JFrame(existingTitle == null ? "New Note" : "Edit Note");
        noteFrame.setSize(400, 300);
        noteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        noteFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new BorderLayout());

        JTextField titleField = new JTextField(20);
        if (existingTitle != null) titleField.setText(existingTitle);

        JTextArea noteTextArea = new JTextArea(10, 30);
        if (existingContent != null) noteTextArea.setText(existingContent);

        JButton saveButton = new JButton("Save Note");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText().trim();
                String content = noteTextArea.getText().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(noteFrame, "Title and Note content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String newNote = title + ": " + content;
                if (existingTitle != null) {
                    // Remove the old note
                    notesModel.removeElement(existingTitle + ": " + existingContent);
                }
                notesModel.addElement(newNote);
                saveNotesToFile();  // Save notes after adding/editing
                JOptionPane.showMessageDialog(noteFrame, "Note saved successfully!");
                noteFrame.dispose();
            }
        });

        inputPanel.add(new JLabel("Title:"), BorderLayout.NORTH);
        inputPanel.add(titleField, BorderLayout.CENTER);

        noteFrame.add(inputPanel, BorderLayout.NORTH);
        noteFrame.add(new JScrollPane(noteTextArea), BorderLayout.CENTER);
        noteFrame.add(saveButton, BorderLayout.SOUTH);
        noteFrame.setVisible(true);
    }

    private void showNotesManager() {
        JFrame notesFrame = new JFrame("Notes Manager");
        notesFrame.setSize(400, 300);
        notesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        notesFrame.setLocationRelativeTo(this);

        JList<String> notesList = new JList<>(notesModel);
        JScrollPane notesScrollPane = new JScrollPane(notesList);

        JButton editNoteButton = new JButton("Edit Selected Note");
        JButton deleteNoteButton = new JButton("Delete Selected Note");

        editNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = notesList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(notesFrame, "No note selected for editing.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String selectedNote = notesModel.getElementAt(selectedIndex);
                String[] noteParts = selectedNote.split(": ", 2);
                String title = noteParts[0];
                String content = noteParts.length > 1 ? noteParts[1] : "";
                showNoteEditor(title, content);
            }
        });

        deleteNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedIndex = notesList.getSelectedIndex();
                    if (selectedIndex == -1) {
                        throw new IllegalArgumentException("No note selected for deletion.");
                    }
                    notesModel.remove(selectedIndex);
                    saveNotesToFile();  // Save notes after deletion
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(notesFrame, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(editNoteButton);
        controlPanel.add(deleteNoteButton);

        notesFrame.add(notesScrollPane, BorderLayout.CENTER);
        notesFrame.add(controlPanel, BorderLayout.SOUTH);
        notesFrame.setVisible(true);
    }

    private void saveNotesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTES_FILE))) {
            for (int i = 0; i < notesModel.size(); i++) {
                writer.write(notesModel.getElementAt(i));
                writer.newLine();  // Write each note on a new line
            }
            JOptionPane.showMessageDialog(this, "Notes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving notes to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNotesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notesModel.addElement(line);
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, no problem
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading notes from file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new PersonallibertyManager();
    }
}