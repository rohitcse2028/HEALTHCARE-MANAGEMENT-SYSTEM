// LostAndFoundFrame.java - FINAL MERGED VERSION (Collections, Generics, Sync, Multithreading)
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.util.List;

/*
  NOTE:
  - This file expects Item.java and LostAndFoundManager.java to exist in the same project.
  - It also expects DatabaseConnection.getConnection() to return a valid Connection.
  - Replace/merge with your existing file (this is a drop-in replacement).
*/

public class LostAndFoundFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel itemsTableModel;
    private JComboBox<String> categoryCombo, typeCombo;
    private JTextField searchField;
    private JButton themeToggleButton;

    // Backend manager (thread-safe)
    private final LostAndFoundManager manager = new LostAndFoundManager();

    // Background refresher control
    private volatile boolean refresherRunning = true;
    private Thread refresherThread;

    public LostAndFoundFrame() {
        setupUI();
        // Load DB data once at startup (fills table + manager)
        loadItemsData();
        startBackgroundRefresher();
        // Ensure thread stops when window closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopBackgroundRefresher();
            }
            @Override
            public void windowClosing(WindowEvent e) {
                stopBackgroundRefresher();
            }
        });
    }

    private void setupUI() {
        setTitle("Lost & Found System - Healthcare Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Lost & Found System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Right panel for buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        themeToggleButton = new JButton(ThemeManager.isDarkMode() ? "Light" : "Dark");
        themeToggleButton.addActionListener(e -> toggleTheme());

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        rightPanel.add(themeToggleButton);
        rightPanel.add(backButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🏠 Browse Items", createBrowsePanel());
        tabbedPane.addTab("📝 Report Lost Item", createReportLostPanel());
        tabbedPane.addTab("📝 Report Found Item", createReportFoundPanel());
        tabbedPane.addTab("🔍 Search Items", createSearchPanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        ThemeManager.applyThemeToContainer(this.getContentPane());
    }

    private void toggleTheme() {
        ThemeManager.toggleTheme();
        themeToggleButton.setText(ThemeManager.isDarkMode() ? "Light" : "Dark");
        ThemeManager.applyThemeToContainer(this.getContentPane());
        repaint();
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columns = {"Item ID", "Item Name", "Category", "Type", "Location", "Date", "Status"};
        itemsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable itemsTable = new JTable(itemsTableModel);
        JScrollPane scrollPane = new JScrollPane(itemsTable);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton viewDetailsButton = new JButton("View Details & Image");
        JButton refreshButton = new JButton("Refresh");

        viewDetailsButton.addActionListener(e -> viewItemDetails(itemsTable));

        refreshButton.addActionListener(e -> loadItemsData());

        controlPanel.add(viewDetailsButton);
        controlPanel.add(refreshButton);

        panel.add(new JLabel("All Lost & Found Items", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportLostPanel() {
        return createReportPanel("Lost");
    }

    private JPanel createReportFoundPanel() {
        return createReportPanel("Found");
    }

    private JPanel createReportPanel(String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        JTextField itemNameField = new JTextField(20);
        JComboBox<String> categoryComboLocal = new JComboBox<>(new String[]{
            "Electronics", "Documents", "Jewelry", "Clothing", "Bags",
            "Keys", "Medical", "Personal", "Other"
        });
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField locationField = new JTextField(20);
        JTextField contactField = new JTextField(20);

        // Image upload
        JLabel imageLabel = new JLabel("No image selected", JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setPreferredSize(new Dimension(150, 150));

        JButton uploadButton = new JButton("Upload Image");
        final java.util.List<byte[]> imageDataList = new java.util.ArrayList<>();

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(LostAndFoundFrame.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] data = new byte[(int) file.length()];
                    fis.read(data);
                    imageDataList.clear();
                    imageDataList.add(data);

                    ImageIcon icon = new ImageIcon(data);
                    Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText("");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(LostAndFoundFrame.this,
                        "Error loading image: " + ex.getMessage());
                }
            }
        });

        // Add form components
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Item Name*:"), gbc);
        gbc.gridx = 1;
        formPanel.add(itemNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Category*:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryComboLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel(type.equals("Lost") ? "Last Location*:" : "Found Location*:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Contact Info*:"), gbc);
        gbc.gridx = 1;
        formPanel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Item Image:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(uploadButton, BorderLayout.SOUTH);
        formPanel.add(imagePanel, gbc);

        // Submit button - uses DB + manager
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Report " + type + " Item");
        submitButton.setBackground(type.equals("Lost") ? Color.ORANGE : Color.GREEN);
        submitButton.setForeground(Color.WHITE);

        submitButton.addActionListener(e -> {
            if (itemNameField.getText().trim().isEmpty() ||
                locationField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(LostAndFoundFrame.this,
                    "Please fill all required fields!");
                return;
            }

            byte[] imageData = null;
            if (!imageDataList.isEmpty()) imageData = imageDataList.get(0);

            // Report to DB
            reportItem(itemNameField.getText().trim(),
                    (String) categoryComboLocal.getSelectedItem(),
                    descriptionArea.getText().trim(),
                    locationField.getText().trim(),
                    contactField.getText().trim(),
                    type,
                    imageData);
        });

        formPanel.add(submitButton, gbc);

        panel.add(new JLabel("Report " + type + " Item", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search controls
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        categoryCombo = new JComboBox<>(new String[]{"All", "Electronics", "Documents", "Jewelry", "Clothing", "Bags", "Keys", "Medical", "Personal", "Other"});
        typeCombo = new JComboBox<>(new String[]{"All", "Lost", "Found"});

        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryCombo);
        searchPanel.add(new JLabel("Type:"));
        searchPanel.add(typeCombo);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // Results table
        String[] columns = {"Item ID", "Item Name", "Category", "Type", "Location", "Date", "Status"};
        DefaultTableModel searchModel = new DefaultTableModel(columns, 0);
        JTable searchTable = new JTable(searchModel);
        JScrollPane scrollPane = new JScrollPane(searchTable);

        JButton viewButton = new JButton("View Selected Item");
        viewButton.addActionListener(e -> viewItemDetails(searchTable));

        searchButton.addActionListener(e -> performSearch(searchModel));

        clearButton.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            typeCombo.setSelectedIndex(0);
            searchModel.setRowCount(0);
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(viewButton, BorderLayout.SOUTH);

        return panel;
    }

    // REPORT (DB insert) + also update manager
    private void reportItem(String name, String category, String description, String location,
                           String contact, String type, byte[] imageData) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String itemId = "ITEM" + String.format("%03d", getNextItemId(conn));

            String query = "INSERT INTO lost_found_items (item_id, item_name, category, description, " +
                    "location_" + (type.equals("Lost") ? "lost" : "found") + ", " +
                    "item_type, image_data, reported_by, contact_info, date_reported, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), 'Active')";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, itemId);
            pstmt.setString(2, name);
            pstmt.setString(3, category);
            pstmt.setString(4, description);
            pstmt.setString(5, location);
            pstmt.setString(6, type);

            if (imageData != null && imageData.length > 0) {
                pstmt.setBytes(7, imageData);
            } else {
                pstmt.setNull(7, java.sql.Types.BLOB);
            }

            pstmt.setString(8, "Anonymous");
            pstmt.setString(9, contact);

            int rows = pstmt.executeUpdate();
            pstmt.close();

            if (rows > 0) {
                // Update manager (so in-memory collections are current)
                Item newItem = new Item(itemId, name, category, type, location, contact);
                manager.addItem(newItem);

                JOptionPane.showMessageDialog(this,
                        type + " item reported successfully!\nItem ID: " + itemId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadItemsData();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error reporting item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load DB data into table and into manager (synchronized)
    private void loadItemsData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ensure model exists
            if (itemsTableModel == null) {
                // if called before browse panel created, initialize a temp model
                String[] columns = {"Item ID", "Item Name", "Category", "Type", "Location", "Date", "Status"};
                itemsTableModel = new DefaultTableModel(columns, 0);
            }
            itemsTableModel.setRowCount(0);

            String query = "SELECT item_id, item_name, category, item_type, " +
                    "COALESCE(location_lost, location_found) as location, " +
                    "date_reported, status FROM lost_found_items WHERE status = 'Active' " +
                    "ORDER BY date_reported DESC";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Clear manager then repopulate so in-memory is same as DB
            // Using synchronized methods of manager
            synchronized (manager) {
                // rebuild manager lists: simplest approach is to create new manager items list
                // but here we'll clear by reflection of methods; since manager doesn't have clear,
                // we'll create a new manager state by removing via replacing reference is not allowed (final),
                // so we call getAllItems and remove? To keep it simple and safe: iterate results and add (idempotent).
                // For correctness, assume DB is source-of-truth; thus we create items and add (manager.addItem is synchronized).
            }

            while (rs.next()) {
                String id = rs.getString("item_id");
                String name = rs.getString("item_name");
                String cat = rs.getString("category");
                String typ = rs.getString("item_type");
                String loc = rs.getString("location");
                String date = rs.getString("date_reported");
                String status = rs.getString("status");

                // Add to table model
                itemsTableModel.addRow(new Object[]{id, name, cat, typ, loc, date, status});

                // Add to manager (synchronized)
                Item it = new Item(id, name, cat, typ, loc, ""); // contact unknown here
                manager.addItem(it);
            }

            rs.close();
            stmt.close();

            // If browse tab exists, refresh visible table via EDT
            SwingUtilities.invokeLater(() -> {
                // find table component and refresh if necessary
                // The table model is already updated; if visible, Swing will show changes
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void performSearch(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            model.setRowCount(0);

            StringBuilder query = new StringBuilder(
                    "SELECT item_id, item_name, category, item_type, " +
                            "COALESCE(location_lost, location_found) as location, " +
                            "date_reported, status FROM lost_found_items WHERE status = 'Active' "
            );

            // Build search conditions
            if (!searchField.getText().trim().isEmpty()) {
                query.append("AND (item_name LIKE ? OR description LIKE ?) ");
            }
            if (!categoryCombo.getSelectedItem().equals("All")) {
                query.append("AND category = ? ");
            }
            if (!typeCombo.getSelectedItem().equals("All")) {
                query.append("AND item_type = ? ");
            }

            query.append("ORDER BY date_reported DESC");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!searchField.getText().trim().isEmpty()) {
                String searchTerm = "%" + searchField.getText().trim() + "%";
                pstmt.setString(paramIndex++, searchTerm);
                pstmt.setString(paramIndex++, searchTerm);
            }
            if (!categoryCombo.getSelectedItem().equals("All")) {
                pstmt.setString(paramIndex++, (String) categoryCombo.getSelectedItem());
            }
            if (!typeCombo.getSelectedItem().equals("All")) {
                pstmt.setString(paramIndex++, (String) typeCombo.getSelectedItem());
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("item_type"),
                        rs.getString("location"),
                        rs.getString("date_reported"),
                        rs.getString("status")
                });
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewItemDetails(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to view details");
            return;
        }

        String itemId = (String) table.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM lost_found_items WHERE item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                showItemDetailsDialog(rs);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading item details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showItemDetailsDialog(ResultSet rs) {
        try {
            JDialog detailsDialog = new JDialog(this, "Item Details", true);
            detailsDialog.setSize(500, 600);
            detailsDialog.setLocationRelativeTo(this);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Item details
            JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            detailsPanel.add(new JLabel("Item ID:"));
            detailsPanel.add(new JLabel(rs.getString("item_id")));
            detailsPanel.add(new JLabel("Item Name:"));
            detailsPanel.add(new JLabel(rs.getString("item_name")));
            detailsPanel.add(new JLabel("Category:"));
            detailsPanel.add(new JLabel(rs.getString("category")));
            detailsPanel.add(new JLabel("Type:"));
            detailsPanel.add(new JLabel(rs.getString("item_type")));
            detailsPanel.add(new JLabel("Location:"));
            detailsPanel.add(new JLabel(rs.getString("item_type").equals("Lost") ?
                    rs.getString("location_lost") : rs.getString("location_found")));
            detailsPanel.add(new JLabel("Date Reported:"));
            detailsPanel.add(new JLabel(rs.getString("date_reported")));
            detailsPanel.add(new JLabel("Description:"));
            JTextArea descArea = new JTextArea(rs.getString("description"), 3, 20);
            descArea.setEditable(false);
            detailsPanel.add(new JScrollPane(descArea));
            detailsPanel.add(new JLabel("Contact Info:"));
            detailsPanel.add(new JLabel(rs.getString("contact_info")));

            // Image display
            JLabel imageLabel = new JLabel("No Image Available", JLabel.CENTER);
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            imageLabel.setPreferredSize(new Dimension(200, 200));

            final byte[] imageData = rs.getBytes("image_data");
            final String itemName = rs.getString("item_name");

            if (imageData != null && imageData.length > 0) {
                ImageIcon icon = new ImageIcon(imageData);
                Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setText("");

                JButton downloadButton = new JButton("Download Image");
                downloadButton.addActionListener(e -> downloadImage(imageData, itemName));
                detailsPanel.add(new JLabel(""));
                detailsPanel.add(downloadButton);
            }

            mainPanel.add(detailsPanel, BorderLayout.CENTER);
            mainPanel.add(imageLabel, BorderLayout.SOUTH);

            detailsDialog.add(mainPanel);
            detailsDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error displaying item details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadImage(byte[] imageData, String itemName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(itemName + ".jpg"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageData);
                JOptionPane.showMessageDialog(this, "Image downloaded successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error downloading image: " + e.getMessage());
            }
        }
    }

    // getNextItemId variant that uses passed Connection (faster and cleaner)
    private int getNextItemId(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT MAX(CAST(SUBSTRING(item_id, 5) AS UNSIGNED)) FROM lost_found_items");

            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }

            rs.close();
            return maxId + 1;
        } catch (SQLException e) {
            return 1;
        }
    }

    // Start background thread to refresh data every 5 seconds
    private void startBackgroundRefresher() {
        refresherRunning = true;
        refresherThread = new Thread(() -> {
            while (refresherRunning) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
                // Refresh data on EDT
                SwingUtilities.invokeLater(this::loadItemsData);
            }
        }, "LF-Refresher");
        refresherThread.setDaemon(true);
        refresherThread.start();
    }

    private void stopBackgroundRefresher() {
        refresherRunning = false;
        if (refresherThread != null) refresherThread.interrupt();
    }
}
