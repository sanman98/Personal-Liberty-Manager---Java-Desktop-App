package personallibertymanager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryManagerWindow {
    public static List<Category> categoriesList = new ArrayList<>();
    private static final String FILE_NAME = "categories.dat";

    public CategoryManagerWindow() {
        loadCategories();
    }

    public void showCategoryManager() {
        JFrame frame = new JFrame("Categories Manager");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Set the background color
        frame.getContentPane().setBackground(new Color(137, 207, 240));

        JLabel titleLabel = new JLabel("Categories and Books:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        frame.add(titleLabel, BorderLayout.NORTH);

        // List of categories
        DefaultListModel<String> categoryListModel = new DefaultListModel<>();
        updateCategoryListModel(categoryListModel);
        JList<String> categoryList = new JList<>(categoryListModel);
        JScrollPane categoryScrollPane = new JScrollPane(categoryList);

        // List of books in selected category
        DefaultListModel<String> booksListModel = new DefaultListModel<>();
        JList<String> booksList = new JList<>(booksListModel);
        JScrollPane booksScrollPane = new JScrollPane(booksList);

        // Panels
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(categoryScrollPane);
        centerPanel.add(booksScrollPane);
        frame.add(centerPanel, BorderLayout.CENTER);

        // Add selection listener to the category list
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = categoryList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Category selectedCategory = categoriesList.get(selectedIndex);
                    updateBooksListModel(selectedCategory, booksListModel);
                }
            }
        });

        // Control buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(137, 207, 240));
        JButton addCategoryButton = new JButton("Add Category");
        JButton deleteCategoryButton = new JButton("Delete Category");
        buttonsPanel.add(addCategoryButton);
        buttonsPanel.add(deleteCategoryButton);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        // Add category button action
        addCategoryButton.addActionListener(e -> {
            String categoryName = JOptionPane.showInputDialog(frame, "Enter Category Name:");
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                categoriesList.add(new Category(categoryName.trim()));
                updateCategoryListModel(categoryListModel);
                saveCategories();
            }
        });

        // Delete category button action
        deleteCategoryButton.addActionListener(e -> {
            int selectedIndex = categoryList.getSelectedIndex();
            if (selectedIndex != -1) {
                categoriesList.remove(selectedIndex);
                updateCategoryListModel(categoryListModel);
                booksListModel.clear(); // Clear books list
                saveCategories();
            }
        });

        frame.setVisible(true);
    }

    private void updateCategoryListModel(DefaultListModel<String> model) {
        model.clear();
        for (Category category : categoriesList) {
            model.addElement(category.getName());
        }
    }

    private void updateBooksListModel(Category category, DefaultListModel<String> booksListModel) {
        booksListModel.clear();
        for (String book : category.getBooks()) {
            booksListModel.addElement(book);
        }
    }

    static void saveCategories() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("categories.dat"))) {
            oos.writeObject(categoriesList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("categories.dat"))) {
            Object data = ois.readObject();
            if (data instanceof List<?>) {
                List<?> rawList = (List<?>) data;
                if (!rawList.isEmpty() && rawList.get(0) instanceof String) {
                    for (Object obj : rawList) {
                        if (obj instanceof String) {
                            categoriesList.add(new Category((String) obj));
                        }
                    }
                } else if (!rawList.isEmpty() && rawList.get(0) instanceof Category) {
                    categoriesList = (List<Category>) rawList;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            categoriesList = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CategoryManagerWindow().showCategoryManager());
    }}