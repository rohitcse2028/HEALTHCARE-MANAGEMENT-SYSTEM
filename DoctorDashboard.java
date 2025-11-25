// DoctorDashboard.java - WITH DARK MODE THEME
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DoctorDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel patientTableModel;
    private JButton themeToggleButton; // NEW: Theme toggle button

    public DoctorDashboard() {
        setupUI();
        loadPatientData();
    }

    private void setupUI() {
        setTitle("Doctor Dashboard - Healthcare Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header with logout button AND theme toggle - MODIFIED
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Doctor Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Right side panel for buttons - NEW
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        // Theme toggle button - NEW
        themeToggleButton = new JButton(ThemeManager.isDarkMode() ? "Light" : "Dark");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 12));
        themeToggleButton.setMargin(new Insets(5, 10, 5, 10));
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(e -> toggleTheme());
        
        // Logout button
        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        
        rightPanel.add(themeToggleButton);
        rightPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
       tabbedPane.addTab("Schedule", createSchedulePanel());
tabbedPane.addTab("Patients", createPatientRecordsPanel());
tabbedPane.addTab("Appointments", createAppointmentsPanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Apply theme to all components - NEW
        ThemeManager.applyThemeToContainer(this.getContentPane());
    }

    // NEW: Theme toggle method
    private void toggleTheme() {
        ThemeManager.toggleTheme();
       themeToggleButton.setText(ThemeManager.isDarkMode() ? "Light" : "Dark");
        ThemeManager.applyThemeToContainer(this.getContentPane());
        repaint();
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Calendar-like schedule view
        String[] columns = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[][] data = {
            {"9:00 AM", "Appointment", "Available", "Appointment", "Available", "Appointment"},
            {"10:00 AM", "Available", "Appointment", "Available", "Appointment", "Available"},
            {"11:00 AM", "Appointment", "Available", "Appointment", "Available", "Appointment"},
            {"2:00 PM", "Available", "Appointment", "Available", "Appointment", "Available"},
            {"3:00 PM", "Appointment", "Available", "Appointment", "Available", "Appointment"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable scheduleTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Update Availability");

        controlPanel.add(updateButton);

        panel.add(new JLabel("Weekly Schedule", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPatientRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Patient ID", "Name", "Age", "Last Visit", "Condition", "Actions"};
        patientTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable patientTable = new JTable(patientTableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewButton = new JButton("View Record");
        JButton updateButton = new JButton("Update Record");
        JButton refreshButton = new JButton("Refresh");

        viewButton.addActionListener(e -> viewPatientRecord(patientTable));
        updateButton.addActionListener(e -> updatePatientRecord(patientTable));
        refreshButton.addActionListener(e -> loadPatientData());

        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);

        panel.add(new JLabel("Patient Records", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Appointment ID", "Patient", "Date", "Time", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Sample appointment data
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT a.appointment_id, u.name as patient_name, " +
             "a.appointment_date, a.appointment_time, a.status " +
             "FROM appointments a " +
             "JOIN patients p ON a.patient_id = p.patient_id " +
             "JOIN users u ON p.user_id = u.user_id " +
             "WHERE a.doctor_id = 'D001' " + // Hardcoded for demo
             "ORDER BY a.appointment_date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("appointment_id"),
                    rs.getString("patient_name"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("status"),
                    "View Details"
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }

        JTable appointmentTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        panel.add(new JLabel("Appointment Overview", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPatientData() {
        try {
            patientTableModel.setRowCount(0);
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT p.patient_id, u.name, TIMESTAMPDIFF(YEAR, p.date_of_birth, CURDATE()) as age, " +
                         "MAX(a.appointment_date) as last_visit, mr.diagnosis " +
                         "FROM patients p " +
                         "JOIN users u ON p.user_id = u.user_id " +
                         "LEFT JOIN appointments a ON p.patient_id = a.patient_id " +
                         "LEFT JOIN medical_records mr ON p.patient_id = mr.patient_id " +
                         "GROUP BY p.patient_id";            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                patientTableModel.addRow(new Object[]{
                    rs.getString("patient_id"),
                    rs.getString("name"),
                    rs.getString("age"),
                    rs.getString("last_visit"),
                    rs.getString("diagnosis"),
                    "View/Edit"
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void viewPatientRecord(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient record to view");
            return;
        }

        String patientName = (String) table.getValueAt(selectedRow, 1);
        JTextArea recordArea = new JTextArea(10, 30);
        recordArea.setText("Medical Record for " + patientName + "\n\n" +
            "Condition: " + table.getValueAt(selectedRow, 4) + "\n" +
            "Last Visit: " + table.getValueAt(selectedRow, 3) + "\n\n" +
            "Treatment History:\n- Regular checkups\n- Medication prescribed\n- Lifestyle recommendations");
        recordArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(recordArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Patient Medical Record", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void updatePatientRecord(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient record to update");
            return;
        }

        JTextField conditionField = new JTextField((String) table.getValueAt(selectedRow, 4));
        JTextArea notesArea = new JTextArea(5, 20);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Condition:"));
        panel.add(conditionField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Patient Record", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            table.setValueAt(conditionField.getText(), selectedRow, 4);
            JOptionPane.showMessageDialog(this, "Patient record updated successfully!");
        }
    }
}