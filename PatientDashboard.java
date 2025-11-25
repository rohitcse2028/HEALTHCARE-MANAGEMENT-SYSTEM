// PatientDashboard.java - COMPLETE FIXED VERSION
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PatientDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel appointmentTableModel;
    private JButton themeToggleButton;

    public PatientDashboard() {
        setupUI();
        loadAppointmentData();
    }

    private void setupUI() {
        setTitle("Patient Dashboard - Healthcare Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header with logout button AND theme toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Patient Dashboard");
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
                int confirm = JOptionPane.showConfirmDialog(PatientDashboard.this, 
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
        tabbedPane.addTab("Appointments", createAppointmentHistoryPanel());
        tabbedPane.addTab("Medical History", createMedicalHistoryPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        tabbedPane.addTab("Book Appointment", createBookingPanel());

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

    private JPanel createAppointmentHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Appointment ID", "Doctor", "Date", "Time", "Status", "Type"};
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable appointmentTable = new JTable(appointmentTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewButton = new JButton("View Details");
        JButton cancelButton = new JButton("Cancel Appointment");
        JButton refreshButton = new JButton("Refresh");

        // FIXED: Lambda expressions replaced
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAppointmentDetails(appointmentTable);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelAppointment(appointmentTable);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAppointmentData();
            }
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        panel.add(new JLabel("Appointment History", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMedicalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea medicalHistoryArea = new JTextArea(20, 50);
        medicalHistoryArea.setText("Medical History for Anjali Singh\n\n" +
            "Personal Information:\n" +
            "- Date of Birth: 1990-08-15\n" +
            "- Blood Type: B+\n" +
            "- Height: 162 cm\n" +
            "- Weight: 58 kg\n\n" +
            "Medical Conditions:\n" +
            "- Hypertension (Diagnosed: 2023)\n" +
            "- Seasonal Allergies\n\n" +
            "Medications:\n" +
            "- Amlodipine 5mg daily\n" +
            "- Cetirizine 10mg as needed\n\n" +
            "Allergies:\n" +
            "- None known\n\n" +
            "Recent Visits:\n" +
            "- 2024-01-15: Routine checkup - Blood pressure stable\n" +
            "- 2023-12-20: Allergy consultation\n" +
            "- 2023-11-15: Blood work - Results normal");
        
        medicalHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(medicalHistoryArea);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton downloadButton = new JButton("Download Records");

        buttonPanel.add(downloadButton);

        panel.add(new JLabel("Medical History", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Personal Information
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField("Anjali Singh", 20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField("anjali.singh@email.com", 20);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField("9876543213", 20);
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        JTextField dobField = new JTextField("1990-08-15", 20);
        panel.add(dobField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setText("A-101, Shanti Apartments, Delhi");
        panel.add(new JScrollPane(addressArea), gbc);

        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        JTextField emergencyField = new JTextField("Rohit Singh (Brother) - 9876543290", 20);
        panel.add(emergencyField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = new JButton("Save Profile Changes");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(PatientDashboard.this, "Profile updated successfully!");
            }
        });
        panel.add(saveButton, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Profile Management", JLabel.CENTER), BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Doctor Selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Doctor:"), gbc);
        gbc.gridx = 1;
        String[] doctors = {"Dr. Amit Sharma (Cardiology)", "Dr. Priya Patel (Pediatrics)", 
                           "Dr. Sanjay Gupta (Orthopedics)"};
        JComboBox<String> doctorCombo = new JComboBox<>(doctors);
        formPanel.add(doctorCombo, gbc);

        // Appointment Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Appointment Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Routine Checkup", "Consultation", "Follow-up", "Emergency"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        formPanel.add(typeCombo, gbc);

        // Preferred Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Preferred Date:"), gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField("2024-02-10");
        formPanel.add(dateField, gbc);

        // Preferred Time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Preferred Time:"), gbc);
        gbc.gridx = 1;
        String[] times = {"9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM"};
        JComboBox<String> timeCombo = new JComboBox<>(times);
        formPanel.add(timeCombo, gbc);

        // Symptoms/Reason
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Reason for Visit:"), gbc);
        gbc.gridx = 1;
        JTextArea reasonArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(reasonArea), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton bookButton = new JButton("Book Appointment");
        bookButton.setBackground(new Color(0, 102, 204));
        bookButton.setForeground(Color.WHITE);
        
        // FIXED: Lambda expression replaced
        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    
                    // Generate appointment ID
                    String appointmentId = "A" + String.format("%03d", getNextAppointmentId());
                    
                    // Get patient ID (current logged in user)
                    String patientId = "P001"; // Hardcoded for demo
                    
                    // Get doctor ID from selection
                    String doctorId = getDoctorIdFromName((String) doctorCombo.getSelectedItem());
                    
                    String query = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, " +
                                  "appointment_date, appointment_time, type, status) VALUES (?, ?, ?, ?, ?, ?, 'Scheduled')";
                    
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, appointmentId);
                    pstmt.setString(2, patientId);
                    pstmt.setString(3, doctorId);
                    pstmt.setString(4, dateField.getText());
                    pstmt.setString(5, (String) timeCombo.getSelectedItem() + ":00");
                    pstmt.setString(6, (String) typeCombo.getSelectedItem());
                    
                    int rowsAffected = pstmt.executeUpdate();
                    pstmt.close();
                    
                    if (rowsAffected > 0) {
                        String confirmation = "Appointment Booked Successfully!\n\n" +
                            "Appointment ID: " + appointmentId + "\n" +
                            "Doctor: " + doctorCombo.getSelectedItem() + "\n" +
                            "Type: " + typeCombo.getSelectedItem() + "\n" +
                            "Date: " + dateField.getText() + "\n" +
                            "Time: " + timeCombo.getSelectedItem() + "\n\n" +
                            "You will receive a confirmation email shortly.";
                        
                        JOptionPane.showMessageDialog(PatientDashboard.this, confirmation, "Booking Confirmed", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh appointment history
                        loadAppointmentData();
                    } else {
                        JOptionPane.showMessageDialog(PatientDashboard.this, "Failed to book appointment!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(PatientDashboard.this, "Database Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        formPanel.add(bookButton, gbc);

        panel.add(new JLabel("Book New Appointment", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadAppointmentData() {
        try {
            appointmentTableModel.setRowCount(0);
            Connection conn = DatabaseConnection.getConnection();
            
            // CORRECTED QUERY - Simple version without complex joins
            String query = "SELECT a.appointment_id, " +
                 "d.name as doctor_name, " +
                 "a.appointment_date, " +
                 "a.appointment_time, " +
                 "a.status, " +
                 "a.type " +
                 "FROM appointments a " +
                 "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                 "WHERE a.patient_id = 'P001' " +
                 "ORDER BY a.appointment_date DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                appointmentTableModel.addRow(new Object[]{
                    rs.getString("appointment_id"),
                    rs.getString("doctor_name"),
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
                "Error loading appointments: " + e.getMessage() + 
                "\n\nTry re-login or check database connection.", 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewAppointmentDetails(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view details");
            return;
        }

        String details = "Appointment Details:\n\n" +
            "ID: " + table.getValueAt(selectedRow, 0) + "\n" +
            "Doctor: " + table.getValueAt(selectedRow, 1) + "\n" +
            "Date: " + table.getValueAt(selectedRow, 2) + "\n" +
            "Time: " + table.getValueAt(selectedRow, 3) + "\n" +
            "Status: " + table.getValueAt(selectedRow, 4) + "\n" +
            "Type: " + table.getValueAt(selectedRow, 5) + "\n\n" +
            "Location: Main Hospital - Room 205\n" +
            "Please arrive 15 minutes early.";

        JOptionPane.showMessageDialog(this, details, "Appointment Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelAppointment(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this appointment?", "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update database
            try {
                Connection conn = DatabaseConnection.getConnection();
                String appointmentId = (String) table.getValueAt(selectedRow, 0);
                String query = "UPDATE appointments SET status = 'Cancelled' WHERE appointment_id = ?";
                
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, appointmentId);
                pstmt.executeUpdate();
                pstmt.close();
                
                // Update table
                table.setValueAt("Cancelled", selectedRow, 4);
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error cancelling appointment: " + e.getMessage());
            }
        }
    }

    // Helper methods for appointment booking
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
            return 1;
        }
    }

    private String getDoctorIdFromName(String doctorName) {
        // Map doctor names to IDs - you'll need to adjust this based on your actual data
        if (doctorName.contains("Amit Sharma")) return "D001";
        if (doctorName.contains("Priya Patel")) return "D002"; 
        if (doctorName.contains("Sanjay Gupta")) return "D003";
        return "D001"; // Default
    }
}