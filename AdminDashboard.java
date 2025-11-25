// AdminDashboard.java - COMPLETE FIXED VERSION
import java.awt.Desktop;
import org.jfree.chart.ChartPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel userTableModel, appointmentTableModel;
    private JButton themeToggleButton;

    public AdminDashboard() {
        setupUI();
        loadUserData();
        loadAppointmentData();
    }

    private void setupUI() {
        setTitle("Admin Dashboard - Healthcare Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header with logout button AND theme toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Right side panel for buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        // Theme toggle button - FIXED
        themeToggleButton = new JButton(ThemeManager.isDarkMode() ? "Light" : "Dark");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 12));
        themeToggleButton.setMargin(new Insets(5, 10, 5, 10));
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });
        
        // Logout button - FIXED
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this, 
                    "Are you sure you want to logout?", "Confirm Logout", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });
        
        rightPanel.add(themeToggleButton);
        rightPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("Appointment Management", createAppointmentManagementPanel());
        tabbedPane.addTab("System Settings", createSystemSettingsPanel());
        tabbedPane.addTab("Analytics", createAnalyticsPanel());
        tabbedPane.addTab("Analytics Charts", new AnalyticsChartsPanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Apply theme to all components
        ThemeManager.applyThemeToContainer(this.getContentPane());
    }

    // Theme toggle method
    private void toggleTheme() {
        ThemeManager.toggleTheme();
        themeToggleButton.setText(ThemeManager.isDarkMode() ? "Light" : "Dark");
        ThemeManager.applyThemeToContainer(this.getContentPane());
        repaint();
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"User ID", "Username", "Name", "Email", "Role", "Status"};
        userTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Buttons - FIXED
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        // FIXED: Lambda expressions replaced
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddUserDialog();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEditUserDialog(userTable);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUser(userTable);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUserData();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        panel.add(new JLabel("User Management", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAppointmentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Appointment ID", "Patient", "Doctor", "Date", "Time", "Status", "Type"};
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable appointmentTable = new JTable(appointmentTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("New Appointment");
        JButton refreshButton = new JButton("Refresh");

        // FIXED: Lambda expressions replaced
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddAppointmentDialog();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAppointmentData();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        panel.add(new JLabel("Appointment Management", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSystemSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("System Name:"));
        JTextField systemNameField = new JTextField("Healthcare Management System");
        panel.add(systemNameField);

        panel.add(new JLabel("Session Timeout (min):"));
        JSpinner timeoutSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
        panel.add(timeoutSpinner);

        panel.add(new JLabel("Max Appointments/Day:"));
        JSpinner maxAppointmentsSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 200, 5));
        panel.add(maxAppointmentsSpinner);

        panel.add(new JLabel("Email Notifications:"));
        JCheckBox emailCheckbox = new JCheckBox("Enable", true);
        panel.add(emailCheckbox);

        panel.add(new JLabel("SMS Notifications:"));
        JCheckBox smsCheckbox = new JCheckBox("Enable", false);
        panel.add(smsCheckbox);

        JButton saveButton = new JButton("Save Settings");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Settings saved successfully!");
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("System Settings", JLabel.CENTER), BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Get statistics
            String[] metrics = {"Total Users", "Active Doctors", "Total Patients", 
                              "Total Appointments", "Pending Appointments", "Today's Appointments"};
            
            String[] values = new String[6];
            
            String[] queries = {
                "SELECT COUNT(*) FROM users",
                "SELECT COUNT(*) FROM users WHERE role='Doctor' AND status='Active'",
                "SELECT COUNT(*) FROM users WHERE role='Patient' AND status='Active'",
                "SELECT COUNT(*) FROM appointments",
                "SELECT COUNT(*) FROM appointments WHERE status='Scheduled'",
                "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()"
            };
            
            Statement stmt = conn.createStatement();
            for (int i = 0; i < queries.length; i++) {
                ResultSet rs = stmt.executeQuery(queries[i]);
                if (rs.next()) {
                    values[i] = String.valueOf(rs.getInt(1));
                }
                rs.close();
            }
            stmt.close();
            
            Object[][] data = new Object[metrics.length][2];
            for (int i = 0; i < metrics.length; i++) {
                data[i][0] = metrics[i];
                data[i][1] = values[i];
            }
            
            String[] columns = {"Metric", "Value"};
            DefaultTableModel model = new DefaultTableModel(data, columns);
            JTable analyticsTable = new JTable(model);
            analyticsTable.setEnabled(false);
            
            JScrollPane scrollPane = new JScrollPane(analyticsTable);
            
            // PDF REPORTS SECTION
            JPanel pdfReportsPanel = new JPanel(new FlowLayout());
            pdfReportsPanel.setBorder(BorderFactory.createTitledBorder("Generate PDF Reports"));
            
            JButton dailyReportBtn = new JButton("Daily Report");
            JButton monthlyReportBtn = new JButton("Monthly Report"); 
            JButton allReportBtn = new JButton("All Appointments Report");
            
            // Button styling
            dailyReportBtn.setBackground(new Color(70, 130, 180));
            dailyReportBtn.setForeground(Color.WHITE);
            monthlyReportBtn.setBackground(new Color(60, 179, 113));
            monthlyReportBtn.setForeground(Color.WHITE);
            allReportBtn.setBackground(new Color(205, 92, 92));
            allReportBtn.setForeground(Color.WHITE);
            
            // FIXED: Lambda expressions replaced
            dailyReportBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generatePDFReport("daily");
                }
            });

            monthlyReportBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generatePDFReport("monthly");
                }
            });

            allReportBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generatePDFReport("all");
                }
            });
            
            pdfReportsPanel.add(dailyReportBtn);
            pdfReportsPanel.add(monthlyReportBtn);
            pdfReportsPanel.add(allReportBtn);
            
            // Main panel layout - Existing analytics + NEW PDF reports
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.add(new JLabel("System Analytics", JLabel.CENTER), BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(pdfReportsPanel, BorderLayout.SOUTH);
            
            panel.add(mainPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading analytics: " + e.getMessage(), JLabel.CENTER));
        }

        return panel;
    }

    // PDF Report Generation Method
    private void generatePDFReport(String reportType) {
        PDFReportService pdfService = new PDFReportService();
        pdfService.generateAppointmentReport(reportType);
    }

    private void loadUserData() {
        try {
            userTableModel.setRowCount(0);
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT user_id, username, name, email, role, status FROM users");
            
            while (rs.next()) {
                userTableModel.addRow(new Object[]{
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("status")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void loadAppointmentData() {
        try {
            appointmentTableModel.setRowCount(0);
            Connection conn = DatabaseConnection.getConnection();
            
            // SIMPLE QUERY - Direct appointments table se
            String query = "SELECT appointment_id, patient_id, doctor_id, " +
                         "appointment_date, appointment_time, status, type " +
                         "FROM appointments " +
                         "ORDER BY appointment_date DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                // Patient aur Doctor names alag se fetch karein
                String patientName = getPatientName(rs.getString("patient_id"));
                String doctorName = getDoctorName(rs.getString("doctor_id"));
                
                appointmentTableModel.addRow(new Object[]{
                    rs.getString("appointment_id"),
                    patientName,
                    doctorName,
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("status"),
                    rs.getString("type")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // HELPER METHOD FOR PATIENT NAME
    private String getPatientName(String patientId) {
        if (patientId == null) return "Unknown Patient";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT name FROM patients WHERE patient_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error getting patient name: " + e.getMessage());
        }
        return "Patient: " + patientId; // Fallback
    }

    // HELPER METHOD FOR DOCTOR NAME  
    private String getDoctorName(String doctorId) {
        if (doctorId == null) return "Unknown Doctor";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT name FROM doctors WHERE doctor_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error getting doctor name: " + e.getMessage());
        }
        return "Doctor: " + doctorId; // Fallback
    }

    private void showAddUserDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Doctor", "Patient"});

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add User", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Validation
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || 
                usernameField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            try {
                Connection conn = DatabaseConnection.getConnection();
                
                // Check if username already exists
                String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, usernameField.getText().trim());
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Username already exists!");
                    rs.close();
                    checkStmt.close();
                    return;
                }
                rs.close();
                checkStmt.close();

                String userId = "U" + String.format("%03d", getNextUserId());
                
                String query = "INSERT INTO users (user_id, username, password, role, name, email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userId);
                pstmt.setString(2, usernameField.getText().trim());
                pstmt.setString(3, new String(passwordField.getPassword()));
                pstmt.setString(4, (String) roleCombo.getSelectedItem());
                pstmt.setString(5, nameField.getText().trim());
                pstmt.setString(6, emailField.getText().trim());
                
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User added successfully!\nUser ID: " + userId);
                    loadUserData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add user!");
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private int getNextUserId() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(user_id, 2) AS UNSIGNED)) FROM users");
            
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            
            rs.close();
            stmt.close();
            return maxId + 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting next user ID: " + e.getMessage());
            return 1; // Default fallback
        }
    }

    private void showEditUserDialog(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }
        
        String userId = (String) table.getValueAt(selectedRow, 0);
        String currentName = (String) table.getValueAt(selectedRow, 2);
        String currentEmail = (String) table.getValueAt(selectedRow, 3);
        String currentRole = (String) table.getValueAt(selectedRow, 4);
        String currentStatus = (String) table.getValueAt(selectedRow, 5);

        JTextField nameField = new JTextField(currentName);
        JTextField emailField = new JTextField(currentEmail);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Doctor", "Patient"});
        roleCombo.setSelectedItem(currentRole);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusCombo.setSelectedItem(currentStatus);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String query = "UPDATE users SET name = ?, email = ?, role = ?, status = ? WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, emailField.getText().trim());
                pstmt.setString(3, (String) roleCombo.getSelectedItem());
                pstmt.setString(4, (String) statusCombo.getSelectedItem());
                pstmt.setString(5, userId);
                
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                    loadUserData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user!");
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        }
    }

    private void deleteUser(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }

        String userId = (String) table.getValueAt(selectedRow, 0);
        String username = (String) table.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user: " + username + "?\nThis action cannot be undone.", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                
                // Check if user has related records
                String checkQuery = "SELECT COUNT(*) FROM doctors WHERE user_id = ? UNION ALL SELECT COUNT(*) FROM patients WHERE user_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, userId);
                checkStmt.setString(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                
                boolean hasRelations = false;
                while (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        hasRelations = true;
                        break;
                    }
                }
                rs.close();
                checkStmt.close();
                
                if (hasRelations) {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot delete user! User has related records in doctors/patients table.");
                    return;
                }

                String query = "DELETE FROM users WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userId);
                
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUserData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user!");
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAddAppointmentDialog() {
        // Create dialog components
        JTextField patientIdField = new JTextField();
        JTextField doctorIdField = new JTextField();
        JTextField dateField = new JTextField("2024-01-20");
        JTextField timeField = new JTextField("10:00:00");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Checkup", "Consultation", "Emergency"});
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Patient ID:"));
        panel.add(patientIdField);
        panel.add(new JLabel("Doctor ID:"));
        panel.add(doctorIdField);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM:SS):"));
        panel.add(timeField);
        panel.add(new JLabel("Appointment Type:"));
        panel.add(typeCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Appointment", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Validate inputs
            if (patientIdField.getText().trim().isEmpty() || doctorIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Patient ID and Doctor ID!");
                return;
            }

            try {
                Connection conn = DatabaseConnection.getConnection();
                
                // Generate appointment ID
                String appointmentId = "A" + String.format("%03d", getNextAppointmentId());
                
                String query = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, type, status) VALUES (?, ?, ?, ?, ?, ?, 'Scheduled')";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, appointmentId);
                pstmt.setString(2, patientIdField.getText().trim());
                pstmt.setString(3, doctorIdField.getText().trim());
                pstmt.setString(4, dateField.getText().trim());
                pstmt.setString(5, timeField.getText().trim());
                pstmt.setString(6, (String) typeCombo.getSelectedItem());
                
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment added successfully!\nAppointment ID: " + appointmentId);
                    loadAppointmentData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add appointment!");
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private int getNextAppointmentId() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(appointment_id, 2) AS UNSIGNED)) FROM appointments");
            
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            
            rs.close();
            stmt.close();
            return maxId + 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting next appointment ID: " + e.getMessage());
            return 1;
        }
    }
}