package personallibertymanager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BooksManager {

    private static final String BOOKS_FILE = "books_data.dat"; // File to save book data
    private List<Category> categoriesList = new ArrayList<>(); // List of categories

    public void showBooksManager() {
        // Use the static list of categories from CategoryManagerWindow
        List<Category> categoriesList = CategoryManagerWindow.categoriesList;
        JFrame booksFrame = new JFrame("Books Manager");
        booksFrame.setSize(400, 300);
        booksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        booksFrame.setLayout(new BorderLayout());

        // Set the background color of the frame to baby blue
        booksFrame.getContentPane().setBackground(new Color(137, 207, 240));

        // Panel to add books
        JPanel addBookPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        addBookPanel.setBackground(new Color(137, 207, 240));
        JLabel nameLabel = new JLabel("Book Name:");
        JTextField nameField = new JTextField(15);
        JLabel authorLabel = new JLabel("Author's Name:");
        JTextField authorField = new JTextField(15);
        addBookPanel.add(nameLabel);
        addBookPanel.add(nameField);
        addBookPanel.add(authorLabel);
        addBookPanel.add(authorField);

        // List of books
        DefaultListModel<String> booksListModel = new DefaultListModel<>();
        JList<String> booksList = new JList<>(booksListModel);
        JScrollPane scrollPane = new JScrollPane(booksList);

        // Button panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(137, 207, 240));
        JButton addBookButton = new JButton("Add Book");
        JButton addBookToCategoryButton = new JButton("Add to Category");
        JButton deleteBookButton = new JButton("Delete Book");
        buttonsPanel.add(addBookButton);
        buttonsPanel.add(addBookToCategoryButton);
        buttonsPanel.add(deleteBookButton);

        booksFrame.add(addBookPanel, BorderLayout.NORTH);
        booksFrame.add(scrollPane, BorderLayout.CENTER);
        booksFrame.add(buttonsPanel, BorderLayout.SOUTH);

        // Load books when starting
        loadBooks(booksListModel);

        // Add book event
        addBookButton.addActionListener(e -> {
            String bookName = nameField.getText();
            String authorName = authorField.getText();

            if (!bookName.isEmpty() && !authorName.isEmpty()) {
                String book = bookName + " by " + authorName;
                booksListModel.addElement(book);
                nameField.setText("");
                authorField.setText("");

                // Save changes
                saveBooks(booksListModel);
            } else {
                JOptionPane.showMessageDialog(booksFrame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete book event
        deleteBookButton.addActionListener(e -> {
            String selectedBook = booksList.getSelectedValue();
            if (selectedBook != null) {
                booksListModel.removeElement(selectedBook);

                // Save changes
                saveBooks(booksListModel);
            } else {
                JOptionPane.showMessageDialog(booksFrame, "Select a book to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add book to category event
        addBookToCategoryButton.addActionListener(e -> {
            String selectedBook = booksList.getSelectedValue();
            if (selectedBook == null) {
                JOptionPane.showMessageDialog(booksFrame, "Select a book to add to a category.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Retrieve categories from the categoriesList
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categoriesList) {
                categoryNames.add(category.getName());
            }

            if (categoryNames.isEmpty()) {
                JOptionPane.showMessageDialog(booksFrame, "No categories available. Add categories first.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedCategory = (String) JOptionPane.showInputDialog(
                booksFrame,
                "Select a category:",
                "Add to Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categoryNames.toArray(),
                categoryNames.get(0)
            );

            if (selectedCategory != null) {
                // Find the selected category and add the book
                for (Category category : categoriesList) {
                    if (category.getName().equals(selectedCategory)) {
                        category.addBook(selectedBook); // Add book to the selected category
                        CategoryManagerWindow.saveCategories(); // Save changes to file
                        JOptionPane.showMessageDialog(booksFrame, "Book added to category: " + selectedCategory);
                        break;
                    }
                }
            }
        });

        booksFrame.setVisible(true);
    }

    // Save books to file
    private void saveBooks(DefaultListModel<String> booksListModel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKS_FILE))) {
            List<String> books = new ArrayList<>();
            for (int i = 0; i < booksListModel.size(); i++) {
                books.add(booksListModel.getElementAt(i));
            }
            oos.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load books from file
    private void loadBooks(DefaultListModel<String> booksListModel) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKS_FILE))) {
            List<String> books = (List<String>) ois.readObject();
            for (String book : books) {
                booksListModel.addElement(book);
            }
        } catch (FileNotFoundException e) {
            // No file yet, no action required
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}